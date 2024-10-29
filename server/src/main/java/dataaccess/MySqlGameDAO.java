package dataaccess;

import model.GameData;

import java.util.Collection;

public class MySqlGameDAO implements GameDAO {
    public void clearGame() {
        ...
    }

    public void createGame(GameData gameData) {
        ...
    }

    public GameData getGame(int gameID) {
        ...
    }

    public Collection<GameData> listGames() {
        ...
    }

    public void updateGame(GameData gameData) {
        ...
    }
}
