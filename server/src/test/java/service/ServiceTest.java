package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServiceTest {

    static private UserDAO userDAO;
    static private GameDAO gameDAO;
    static private AuthDAO authDAO;
    static private UserService userService;
    static private GameService gameService;

    @BeforeAll
    public static void init() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);
    }

    @BeforeEach
    public void clear() {
        userService.clear();
        gameService.clear();
    }

    @Test
    public void registerUserSuccess() throws Exception {
        UserData newUser = new UserData("username", "password", "email@gmail.com");
        AuthData registrationResult = userService.registerUser(newUser);
        Assertions.assertEquals(newUser, userDAO.getUser(newUser.username()));
        Assertions.assertEquals(registrationResult, authDAO.getAuth(registrationResult.authToken()));
    }

    @Test
    public void registerUserFailure() throws Exception {
        UserData newUser = new UserData("username", "password1", "email@gmail.com");
        userService.registerUser(newUser);
        UserData duplicateUser = new UserData("username", "password2", "newemail@gmail.com");
        Assertions.assertThrows(ServiceException.class, () -> userService.registerUser(duplicateUser));
    }

    @Test
    public void loginUserSuccess() throws Exception {
        UserData newUser = new UserData("username", "password", "email@gmail.com");
        userService.registerUser(newUser);
        AuthData loginResult = userService.loginUser(newUser);
        Assertions.assertEquals(loginResult, authDAO.getAuth(loginResult.authToken()));
    }

    @Test
    public void loginUserFailure() throws Exception {
        UserData newUser = new UserData("username1", "password1", "email@gmail.com");
        userService.registerUser(newUser);
        UserData wrongUser1 = new UserData("username1", "password2", null);
        UserData wrongUser2 = new UserData("username2", "password1", null);
        Assertions.assertThrows(ServiceException.class, () -> userService.loginUser(wrongUser1));
        Assertions.assertThrows(ServiceException.class, () -> userService.loginUser(wrongUser2));
    }

    @Test
    public void logoutUserSuccess() throws Exception {
        UserData newUser = new UserData("username", "password", "email@gmail.com");
        userService.registerUser(newUser);
        AuthData loginResult = userService.loginUser(newUser);
        userService.logoutUser(loginResult.authToken());
        Assertions.assertNull(authDAO.getAuth(loginResult.authToken()));
    }

    @Test
    public void logoutUserFailure() throws Exception {
        UserData newUser = new UserData("username1", "password1", "email@gmail.com");
        userService.registerUser(newUser);
        userService.loginUser(newUser);
        AuthData badAuth = new AuthData("badAuthToken", newUser.username());
        Assertions.assertThrows(ServiceException.class, () -> userService.logoutUser(badAuth.authToken()));
    }

    @Test
    public void clearUserTest() throws Exception {
        userService.clear();
        UserData newUser1 = new UserData("username1", "password1", "email1@gmail.com");
        UserData newUser2 = new UserData("username2", "password2", "email2@gmail.com");
        UserData newUser3 = new UserData("username3", "password3", "email3@gmail.com");
        userService.registerUser(newUser1);
        userService.registerUser(newUser2);
        userService.registerUser(newUser3);
        userService.clear();
        Assertions.assertNull(userDAO.getUser(newUser1.username()));
        Assertions.assertNull(userDAO.getUser(newUser2.username()));
        Assertions.assertNull(userDAO.getUser(newUser3.username()));
    }

    @Test
    public void createGameSuccess() throws Exception {
        UserData newUser = new UserData("username", "password", "email@gmail.com");
        GameData requestGame = new GameData(0, null, null, "game1", null);
        AuthData registrationResult = userService.registerUser(newUser);
        GameData resultGame = gameService.createGame(registrationResult.authToken(), requestGame);
        Assertions.assertEquals(requestGame.gameName(), gameDAO.getGame(resultGame.gameID()).gameName());
    }

    @Test
    public void createGameFailure() throws Exception {
        UserData newUser = new UserData("username", "password", "email@gmail.com");
        GameData requestGame = new GameData(0, null, null, null, null);
        AuthData registrationResult = userService.registerUser(newUser);
        Assertions.assertThrows(ServiceException.class, () ->
                gameService.createGame(registrationResult.authToken(), requestGame));
    }
}
