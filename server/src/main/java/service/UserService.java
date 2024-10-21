package service;

import dataaccess.*;
import model.*;

import java.util.Objects;
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

    public AuthData registerUser(UserData newUser) throws ServiceException {
        if (newUser.username() == null || newUser.password() == null || newUser.email() == null) {
            throw new ServiceException(400, "Error: bad request.");
        }
        if (userDAO.getUser(newUser.username()) != null) {
            throw new ServiceException(403, "Error: already taken.");
        }
        userDAO.createUser(newUser);
        AuthData newAuth = new AuthData(UUID.randomUUID().toString(), newUser.username());
        authDAO.createAuth(newAuth);
        return newAuth;
    }

    public AuthData loginUser(UserData user) throws ServiceException {
        if (user.username() == null || user.password() == null) {
            throw new ServiceException(500, "Please provide username and password.");
        }
        if (userDAO.getUser(user.username()) == null) {
            throw new ServiceException(401, "Error: unauthorized.");
        }
        if (!Objects.equals(userDAO.getUser(user.username()).password(), user.password())) {
            throw new ServiceException(401, "Error: unauthorized");
        }
        userDAO.getUser(user.username());
        AuthData newAuth = new AuthData(UUID.randomUUID().toString(), user.username());
        authDAO.createAuth(newAuth);
        return newAuth;
    }

//    public void logoutUser(AuthData authdata) throws ServiceException {
//
//    }
}
