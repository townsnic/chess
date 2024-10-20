package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO extends DataAccess {
    void createGame(GameData gameData) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    Collection<GameData> listGames();

    void updateGame(GameData gameData) throws DataAccessException;
}
