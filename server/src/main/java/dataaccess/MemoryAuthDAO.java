package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    final private HashMap<String, AuthData> authDataMap = new HashMap<>();

    public void clearAuth() {
        authDataMap.clear();
    }

    public AuthData createAuth(String username) {
        AuthData newAuth = new AuthData(UUID.randomUUID().toString(), username);
        authDataMap.put(newAuth.authToken(), newAuth);
        return newAuth;
    }

    public AuthData getAuth(String authToken) {
        return authDataMap.get(authToken);
    }

    public void deleteAuth(String authToken) {
        authDataMap.remove(authToken);
    }
}
