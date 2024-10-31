package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;
import server.JoinRequest;

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
            throw new ServiceException(401, "Error: unauthorized");
        }
        return gameDAO.listGames();
    }

    public GameData createGame(String authToken, GameData gameData) throws Exception {
        String gameName = gameData.gameName();

        if (gameName == null) {
            throw new ServiceException(400, "Error: bad request");
        }
        if (authDAO.getAuth(authToken) == null) {
            throw new ServiceException(401, "Error: unauthorized");
        }
        return gameDAO.createGame(gameData);
    }

    public void joinGame(String authToken, JoinRequest joinRequest) throws Exception {
        ChessGame.TeamColor playerColor = joinRequest.playerColor();
        int gameID = joinRequest.gameID();
        GameData gameToJoin = gameDAO.getGame(gameID);

        if (playerColor != ChessGame.TeamColor.WHITE && playerColor != ChessGame.TeamColor.BLACK) {
            throw new ServiceException(400, "Error: bad request");
        }
        if (gameToJoin == null) {
            throw new ServiceException(400, "Error: bad request");
        }
        if (authDAO.getAuth(authToken) == null) {
            throw new ServiceException(401, "Error: unauthorized");
        }
        if (playerColor == ChessGame.TeamColor.WHITE) {
            if (gameToJoin.whiteUsername() != null) {
                throw new ServiceException(403, "Error: already taken");
            }
        } else {
            if (gameToJoin.blackUsername() != null) {
                throw new ServiceException(403, "Error: already taken");
            }
        }

        String username = authDAO.getAuth(authToken).username();
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
