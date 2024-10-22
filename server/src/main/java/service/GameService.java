package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;
import server.GameRequest;

import java.util.Collection;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthService authService;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        authService = new AuthService(authDAO);
    }

    public void clear() {
        gameDAO.clear();
    }

    public Collection<GameData> listGames(String authToken) throws ServiceException {
        if (authService.getAuth(authToken) == null) {
            throw new ServiceException(401, "Error: unauthorized");
        }
        return gameDAO.listGames();
    }

    public GameData createGame(String authToken, GameData gameData) throws ServiceException {
        if (gameData.gameName() == null) {
            throw new ServiceException(400, "Error: bad request");
        }
        if (authService.getAuth(authToken) == null) {
            throw new ServiceException(401, "Error: unauthorized");
        }
        GameData newGame = new GameData((gameDAO.listGames().size() + 1),
                null, null, gameData.gameName(), new ChessGame());
        gameDAO.createGame(newGame);
        return newGame;
    }

    public void joinGame(String authToken, GameRequest gameRequest) throws ServiceException {
        ChessGame.TeamColor playerColor = gameRequest.playerColor();
        int gameID = gameRequest.gameID();
        GameData gameToJoin = gameDAO.getGame(gameID);

        if (gameRequest.playerColor() != ChessGame.TeamColor.WHITE &&
                gameRequest.playerColor() != ChessGame.TeamColor.BLACK) {
            throw new ServiceException(400, "Error: bad request");
        }
        if (gameToJoin == null) {
            throw new ServiceException(400, "Error: bad request");
        }
        if (authService.getAuth(authToken) == null) {
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

        String username = authService.getAuth(authToken).username();
        GameData updatedGame;
        if (playerColor == ChessGame.TeamColor.WHITE) {
            updatedGame = new GameData(gameID, username,
                    gameToJoin.blackUsername(), gameToJoin.gameName(), gameToJoin.game());
        } else {
            updatedGame = new GameData(gameID, gameToJoin.whiteUsername(),
                    username, gameToJoin.gameName(), gameToJoin.game());
        }
        gameDAO.updateGame(updatedGame);
    }
}
