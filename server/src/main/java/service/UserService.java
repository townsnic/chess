package service;

import dataaccess.*;
import model.*;

import java.util.Objects;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public void clear() {
        userDAO.clearUser();
        authDAO.clearAuth();
    }

    public AuthData registerUser(UserData newUser) throws ServiceException {
        String username = newUser.username();

        if (username == null || newUser.password() == null || newUser.email() == null) {
            throw new ServiceException(400, "Error: bad request.");
        }
        if (userDAO.getUser(username) != null) {
            throw new ServiceException(403, "Error: already taken.");
        }
        userDAO.createUser(newUser);
        return authDAO.createAuth(username);
    }

    public AuthData loginUser(UserData user) throws ServiceException {
        String username = user.username();

        if (userDAO.getUser(username) == null) {
            throw new ServiceException(401, "Error: unauthorized.");
        }
        if (!Objects.equals(userDAO.getUser(username).password(), user.password())) {
            throw new ServiceException(401, "Error: unauthorized");
        }
        return authDAO.createAuth(username);
    }

    public void logoutUser(String authToken) throws ServiceException {
        if (authDAO.getAuth(authToken) == null) {
            throw new ServiceException(401, "Error: unauthorized");
        }
        authDAO.deleteAuth(authToken);
    }
}
