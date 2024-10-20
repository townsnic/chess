package dataaccess;

import model.UserData;

public interface UserDAO extends DataAccess {
    void createUser(UserData userData) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;
}
