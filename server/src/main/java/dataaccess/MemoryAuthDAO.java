package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    final private HashMap<String, AuthData> authDataMap = new HashMap<>();

    public void clearAuth() {
        authDataMap.clear();
    }

    public void createAuth(AuthData authData) throws DataAccessException {
        if (authDataMap.containsKey(authData.authToken())) {
            throw new DataAccessException("Token already exists!");
        }
        authDataMap.put(authData.authToken(), authData);
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        if (!authDataMap.containsKey(authToken)) {
            throw new DataAccessException("Token doesn't exist!");
        }
        return authDataMap.get(authToken);
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        if (!authDataMap.containsKey(authToken)) {
            throw new DataAccessException("Token doesn't exist!");
        }
        authDataMap.remove(authToken);
    }
}
