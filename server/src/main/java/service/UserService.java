package service;

import dataaccess.*;
import model.*;

public class UserService {
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public UserData registerUser(UserData newUser) throws DataAccessException {
        return newUser;
    }

    // login
    // logout
}
