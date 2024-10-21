package dataaccess;

import model.UserData;

public interface UserDAO extends DataAccess {
    void createUser(UserData userData);

    UserData getUser(String username);
}
