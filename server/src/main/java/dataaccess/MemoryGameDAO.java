package dataaccess;

import model.AuthData;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    final private HashMap<Integer, GameData> gameDataMap = new HashMap<>();

    public void clearGame() {
        gameDataMap.clear();
    }

    public void createGame(GameData gameData) throws DataAccessException {
        if (gameDataMap.containsKey(gameData.gameID())) {
            throw new DataAccessException("Game ID already exists!");
        }
        gameDataMap.put(gameData.gameID(), gameData);
    }

    public GameData getGame(int gameID) throws DataAccessException {
        if (!gameDataMap.containsKey(gameID)) {
            throw new DataAccessException("Game ID doesn't exist!");
        }
        return gameDataMap.get(gameID);
    }

    public Collection<GameData> listGames() {
        return gameDataMap.values();
    }

    public void updateGame(GameData gameData) throws DataAccessException {
        if (!gameDataMap.containsKey(gameData.gameID())) {
            throw new DataAccessException("Game ID doesn't exist!");
        }
        // Fix this
        GameData dataToUpdate = gameDataMap.get(gameData.gameID());

    }
}