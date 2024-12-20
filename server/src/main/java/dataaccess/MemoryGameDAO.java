package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    final private HashMap<Integer, GameData> gameDataMap = new HashMap<>();

    public void clearGame() {
        gameDataMap.clear();
    }

    public GameData createGame(GameData gameData) {
        GameData newGame = new GameData((gameDataMap.size() + 1),
                null, null, gameData.gameName(), gameData.game());
        gameDataMap.put(newGame.gameID(), newGame);
        return newGame;
    }

    public GameData getGame(int gameID) {
        return gameDataMap.get(gameID);
    }

    public Collection<GameData> listGames() {
        return gameDataMap.values();
    }

    public void updateGame(GameData gameData) {
        gameDataMap.put(gameData.gameID(), gameData);
    }
}