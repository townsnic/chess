package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DataAccessTest {

    static private UserDAO userDAO;

    @BeforeAll
    public static void init() throws DataAccessException {
        userDAO = new MySqlUserDAO();
    }

    @BeforeEach
    public void clear() throws DataAccessException {
        userDAO.clearUser();
    }

    @Test
    public void createUserSuccess() throws DataAccessException {
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
}
