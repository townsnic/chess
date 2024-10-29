package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ServiceException;

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
        UserData expected = new UserData("username", "password", "email@gmail.com");
        userDAO.createUser(expected);
        UserData actual = userDAO.getUser("username");
        Assertions.assertEquals(actual, expected);
    }

    @Test
    public void createUserFailure() throws DataAccessException {
        UserData user = new UserData("username1", "password1", "email1@gmail.com");
        UserData duplicateUser = new UserData("username1", "password1", "email1@gmail.com");
        userDAO.createUser(user);
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.createUser(duplicateUser));
    }
}
