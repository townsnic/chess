package dataaccess;

import model.AuthData;

public class MySqlAuthDAO implements AuthDAO {
    public void clearAuth() {
        ...
    }

    public AuthData createAuth(String username) {
        ...
    }

    public AuthData getAuth(String authToken) {
        ...
    }

    public void deleteAuth(String authToken) {
        ...
    }
}
