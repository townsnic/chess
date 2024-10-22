package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    void clearGame();

    void createGame(GameData gameData);

    GameData getGame(int gameID);

    Collection<GameData> listGames();

    void updateGame(GameData gameData);
}
