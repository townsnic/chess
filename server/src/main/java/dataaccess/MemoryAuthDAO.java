package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    final private HashMap<String, AuthData> authDataMap = new HashMap<>();

    public void clear() {
        authDataMap.clear();
    }

    public void createAuth(AuthData authData) {
        authDataMap.put(authData.authToken(), authData);
    }

    public AuthData getAuth(String authToken) {
        return authDataMap.get(authToken);
    }

    public void deleteAuth(String authToken) {
        authDataMap.remove(authToken);
    }
}
