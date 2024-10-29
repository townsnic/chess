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
        UserData expected = new UserData("username", "password", "email@gmail.com");
        userDAO.createUser(expected);
        UserData actual = userDAO.getUser("username");
        Assertions.assertEquals(actual, expected);
    }
}
