package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ServiceTest {

    static private UserDAO userDAO;
    static private AuthDAO authDAO;
    static private UserService userService;

    @BeforeAll
    public static void init() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);
    }

    @Test
    public void registerUser() throws Exception {
        UserData newUser = new UserData("username", "password", "email@gmail.com");
        AuthData registrationResult = userService.registerUser(newUser);
        Assertions.assertEquals(newUser, userDAO.getUser(newUser.username()));
    }
}
