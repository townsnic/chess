package dataaccess;

import chess.ChessGame;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Collection;

public class DataAccessTest {

    static private UserDAO userDAO;
    static private AuthDAO authDAO;
    static private GameDAO gameDAO;

    @BeforeAll
    public static void init() throws DataAccessException {
        userDAO = new MySqlUserDAO();
        authDAO = new MySqlAuthDAO();
        gameDAO = new MySqlGameDAO();
    }

    @BeforeEach
    public void clear() throws DataAccessException {
        userDAO.clearUser();
        authDAO.clearAuth();
        gameDAO.clearGame();
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
        Assertions.assertTrue(BCrypt.checkpw(expected.password(), actual.password()));
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
    public void clearUserSuccess() throws DataAccessException {
        UserData user = new UserData("username", "password", "email@gmail.com");
        userDAO.createUser(user);
        userDAO.clearUser();
        Assertions.assertNull(userDAO.getUser(user.username()));
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

    @Test
    public void clearAuthSuccess() throws DataAccessException {
        AuthData auth = authDAO.createAuth("username");
        authDAO.clearAuth();
        Assertions.assertNull(authDAO.getAuth(auth.authToken()));
    }

    @Test
    public void createGameSuccess() {
        GameData game = new GameData(0, null, null, "Cool Name", new ChessGame());
        Assertions.assertDoesNotThrow(() -> gameDAO.createGame(game));
    }

    @Test
    public void createGameFailure() throws DataAccessException {
        GameData game = new GameData(0, null, null, null, null);
        Assertions.assertNull(gameDAO.createGame(game).gameName());
    }

    @Test
    public void getGameSuccess() throws DataAccessException {
        GameData expected = gameDAO.createGame(new GameData(0, null, null,
                "Cool Name", new ChessGame()));
        GameData actual = gameDAO.getGame(expected.gameID());
        Assertions.assertEquals(actual, expected);
    }

    @Test
    public void getGameFailure() throws DataAccessException {
        GameData game1 = gameDAO.createGame(new GameData(0, null, null,
                "Cool Name 1", new ChessGame()));
        GameData game2 = gameDAO.createGame(new GameData(0, null, null,
                "Cool Name 2", new ChessGame()));
        Assertions.assertNull(gameDAO.getGame(3));
    }

    @Test
    public void listGameSuccess() throws DataAccessException {
        GameData game1 = gameDAO.createGame(new GameData(0, null, null,
                "Cool Name 1", new ChessGame()));
        GameData game2 = gameDAO.createGame(new GameData(0, null, null,
                "Cool Name 2", new ChessGame()));
        GameData game3 = gameDAO.createGame(new GameData(0, null, null,
                "Cool Name 3", new ChessGame()));
        Collection<GameData> games = gameDAO.listGames();
        Assertions.assertTrue(games.contains(game1));
        Assertions.assertTrue(games.contains(game2));
        Assertions.assertTrue(games.contains(game3));
    }

    @Test
    public void listGameFailure() throws DataAccessException {
        Collection<GameData> games = gameDAO.listGames();
        Assertions.assertTrue(games.isEmpty());
    }

    @Test
    public void updateGameSuccess() throws DataAccessException {
        GameData initialGame = gameDAO.createGame(new GameData(0, null, null,
                "Cool Name 1", new ChessGame()));
        GameData updatedGame = new GameData(1, "Cool User", null,
                "Cool Name 1", new ChessGame());
        gameDAO.updateGame(updatedGame);
        Assertions.assertEquals(updatedGame, gameDAO.getGame(1));

    }

    @Test
    public void updateGameFailure() throws DataAccessException {
        GameData initialGame = gameDAO.createGame(new GameData(0, null, null,
                "Cool Name 1", new ChessGame()));
        GameData updatedGame = new GameData(2, "Cool User", null,
                "Cool Name 1", new ChessGame());
        Assertions.assertEquals(initialGame, gameDAO.getGame(1));
    }

    @Test
    public void clearGameSuccess() throws DataAccessException {
        GameData game = gameDAO.createGame(new GameData(0, null, null, "Cool Name", new ChessGame()));
        gameDAO.clearGame();
        Assertions.assertNull(gameDAO.getGame(game.gameID()));
    }
}
