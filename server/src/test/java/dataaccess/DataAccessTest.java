package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DataAccessTest {

    static private UserDAO userDAO;
    static private AuthDAO authDAO;

    @BeforeAll
    public static void init() throws DataAccessException {
        userDAO = new MySqlUserDAO();
        authDAO = new MySqlAuthDAO();
    }

    @BeforeEach
    public void clear() throws DataAccessException {
        userDAO.clearUser();
        authDAO.clearAuth();
    }

    @Test
    public void createUserSuccess() {
        UserData user = new UserData("username", "password", "email@gmail.com");
        Assertions.assertDoesNotThrow(() -> userDAO.createUser(user));
    }

    @Test
    public void createUserFailure() throws DataAccessException {
        UserData user = new UserData("username1", "password1", "email1@gmail.com");
        UserData duplicateUser = new UserData("username1", "password1", "email1@gmail.com");
        userDAO.createUser(user);
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.createUser(duplicateUser));
    }

    @Test
    public void getUserSuccess() throws DataAccessException {
        UserData expected = new UserData("username", "password", "email@gmail.com");
        userDAO.createUser(expected);
        UserData actual = userDAO.getUser("username");
        Assertions.assertEquals(actual, expected);
    }

    @Test
    public void getUserFailure() throws DataAccessException {
        UserData user1 = new UserData("username1", "password1", "email1@gmail.com");
        UserData user2 = new UserData("username2", "password2", "email2@gmail.com");
        userDAO.createUser(user1);
        UserData result = userDAO.getUser(user2.username());
        Assertions.assertNull(result);
    }

    @Test
    public void createAuthSuccess() {
        Assertions.assertDoesNotThrow(() -> authDAO.createAuth("username"));
    }

    @Test
    public void createAuthFailure() {
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.createAuth(null));
    }

    @Test
    public void getAuthSuccess() throws DataAccessException {
        AuthData expected = authDAO.createAuth("username");
        AuthData actual = authDAO.getAuth(expected.authToken());
        Assertions.assertEquals(actual, expected);
    }

    @Test
    public void getAuthFailure() throws DataAccessException {
        authDAO.createAuth("username");
        Assertions.assertNull(authDAO.getAuth("badToken"));
    }

    @Test
    public void deleteAuthSuccess() throws DataAccessException {
        AuthData auth = authDAO.createAuth("username");
        authDAO.deleteAuth(auth.authToken());
        Assertions.assertNull(authDAO.getAuth(auth.authToken()));
    }

    @Test
    public void deleteAuthFailure() throws DataAccessException {
        AuthData auth = authDAO.createAuth("username");
        authDAO.deleteAuth("badToken");
        Assertions.assertEquals(authDAO.getAuth(auth.authToken()), auth);
    }
}
