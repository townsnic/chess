package service;

import dataaccess.*;
import model.*;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public void clear() {
        userDAO.clear();
    }

    public AuthData registerUser(UserData newUser) throws Exception {
        if (newUser.username() == null || newUser.password() == null || newUser.email() == null) {
            throw new ServiceException("Please provide username, password, and email.");
        }
        if (userDAO.getUser(newUser.username()) != null) {
            throw new ServiceException("Username already in use.");
        }
        userDAO.createUser(newUser);
        AuthData newAuth = new AuthData(UUID.randomUUID().toString(), newUser.username());
        authDAO.createAuth(newAuth);
        return newAuth;
    }

    public AuthData loginUser(UserData user) throws DataAccessException {
        userDAO.getUser(user.username());
        AuthData newAuth = new AuthData(UUID.randomUUID().toString(), user.username());
        authDAO.createAuth(newAuth);
        return newAuth;
    }

    // logout
}
