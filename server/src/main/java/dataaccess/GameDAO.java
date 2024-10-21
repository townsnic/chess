package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO extends DataAccess {
    void createGame(GameData gameData);

    GameData getGame(int gameID);

    Collection<GameData> listGames();

    void updateGame(GameData gameData);
}
