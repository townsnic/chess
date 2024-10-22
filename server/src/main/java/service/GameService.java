package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;

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
}
