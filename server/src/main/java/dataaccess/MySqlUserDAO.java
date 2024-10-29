package dataaccess;

import model.UserData;

public class MySqlUserDAO implements UserDAO {
    public void clearUser() {
        ...
    }

    public void createUser(UserData userData) {
        ...
    }

    public UserData getUser(String username) {
        ...
    }
}
