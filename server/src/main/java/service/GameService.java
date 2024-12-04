package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;

import java.util.Collection;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public void clear() throws DataAccessException {
        gameDAO.clearGame();
        authDAO.clearAuth();
    }

    public Collection<GameData> listGames(String authToken) throws Exception {
        if (authDAO.getAuth(authToken) == null) {
            throw new ServiceException(401, "Error: Unauthorized");
        }
        return gameDAO.listGames();
    }

    public GameData createGame(String authToken, GameData gameData) throws Exception {
        String gameName = gameData.gameName();

        if (gameName == null) {
            throw new ServiceException(400, "Error: Please provide a name for the game.");
        }
        if (authDAO.getAuth(authToken) == null) {
            throw new ServiceException(401, "Error: Unauthorized");
        }
        return gameDAO.createGame(gameData);
    }

    public void joinGame(String authToken, JoinRequest joinRequest) throws Exception {
        ChessGame.TeamColor playerColor = joinRequest.playerColor();
        int gameID = joinRequest.gameID();
        GameData gameToJoin = gameDAO.getGame(gameID);

        if (playerColor != ChessGame.TeamColor.WHITE && playerColor != ChessGame.TeamColor.BLACK) {
            throw new ServiceException(400, "Error: Please provide a valid color.");
        }
        if (gameToJoin == null) {
            throw new ServiceException(400, "Error: Please select a valid game.");
        }
        if (authDAO.getAuth(authToken) == null) {
            throw new ServiceException(401, "Error: Unauthorized");
        }
        String username = authDAO.getAuth(authToken).username();
        if (playerColor == ChessGame.TeamColor.WHITE) {
            if (!(gameToJoin.whiteUsername() == null || gameToJoin.whiteUsername().equals(username))) {
                throw new ServiceException(403, "Error: The selected color is already taken.");
            }
        } else {
            if (!(gameToJoin.blackUsername() == null || gameToJoin.blackUsername().equals(username))) {
                throw new ServiceException(403, "Error: The selected color is already taken.");
            }
        }

        String gameName = gameToJoin.gameName();
        ChessGame game = gameToJoin.game();
        GameData updatedGame;

        if (playerColor == ChessGame.TeamColor.WHITE) {
            updatedGame = new GameData(gameID, username, gameToJoin.blackUsername(), gameName, game);
        } else {
            updatedGame = new GameData(gameID, gameToJoin.whiteUsername(), username, gameName, game);
        }
        gameDAO.updateGame(updatedGame);
    }
}
