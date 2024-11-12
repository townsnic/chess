package service;

import dataaccess.*;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public void clear() throws DataAccessException {
        userDAO.clearUser();
        authDAO.clearAuth();
    }

    public AuthData registerUser(UserData newUser) throws Exception {
        String username = newUser.username();

        if (username == null || newUser.password() == null || newUser.email() == null) {
            throw new ServiceException(400, "Error: Please provide username, password, and email.");
        }
        if (userDAO.getUser(username) != null) {
            String errorMessage = String.format("Error: The username %s is already in use.", username);
            throw new ServiceException(403, errorMessage);
        }
        userDAO.createUser(newUser);
        return authDAO.createAuth(username);
    }

    public AuthData loginUser(UserData user) throws Exception {
        String username = user.username();

        if (userDAO.getUser(username) == null) {
            throw new ServiceException(401, "Error: The provided username does not exist.");
        }
        if (!BCrypt.checkpw(user.password(), userDAO.getUser(username).password())) {
            throw new ServiceException(401, "Error: The provided password is incorrect.");
        }
        return authDAO.createAuth(username);
    }

    public void logoutUser(String authToken) throws Exception {
        if (authDAO.getAuth(authToken) == null) {
            throw new ServiceException(401, "Error: Unauthorized");
        }
        authDAO.deleteAuth(authToken);
    }
}
