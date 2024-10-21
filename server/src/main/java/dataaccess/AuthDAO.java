package dataaccess;

import model.AuthData;

public interface AuthDAO extends DataAccess {
    void createAuth(AuthData authData);

    AuthData getAuth(String authToken);

    void deleteAuth(String authData);
}
