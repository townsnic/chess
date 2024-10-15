package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void clearAuth();

    void createAuth(AuthData authData) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void deleteAuth(String authData) throws DataAccessException;
}
