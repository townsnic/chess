package service;

import dataaccess.*;
import model.*;

import java.util.Objects;

public class UserService {
    private final UserDAO userDAO;
    private final AuthService authService;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        authService = new AuthService(authDAO);
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
        return authService.createAuth(newUser.username());
    }

    public AuthData loginUser(UserData user) throws ServiceException {
        if (userDAO.getUser(user.username()) == null) {
            throw new ServiceException(401, "Error: unauthorized.");
        }
        if (!Objects.equals(userDAO.getUser(user.username()).password(), user.password())) {
            throw new ServiceException(401, "Error: unauthorized");
        }
        return authService.createAuth(user.username());
    }

    public void logoutUser(String authToken) throws ServiceException {
        if (authService.getAuth(authToken) == null) {
            throw new ServiceException(401, "Error: unauthorized");
        }
        authService.deleteAuth(authToken);
    }
}
