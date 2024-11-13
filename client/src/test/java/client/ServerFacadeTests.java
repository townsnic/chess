package client;

import chess.ChessGame;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import serverfacade.ServerFacade;

import java.util.Collection;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        int port = server.run(0);
        String address = String.format("http://localhost:%s", port);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(address);
    }

    @BeforeEach
    public void clear() throws Exception {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerSuccess() throws Exception {
        UserData newUser = new UserData("username", "password", "email@gmail.com");
        AuthData auth = facade.register(newUser);
        Assertions.assertNotNull(auth);
    }

    @Test
    public void registerFailure() {
        UserData newUser = new UserData("username", "password", null);
        Assertions.assertThrows(Exception.class, () -> facade.register(newUser));
    }

    @Test
    public void loginSuccess() throws Exception {
        UserData newUser = new UserData("username", "password", "email@gmail.com");
        facade.register(newUser);
        AuthData auth = facade.login(newUser);
        Assertions.assertNotNull(auth);
    }

    @Test
    public void loginFailure() {
        UserData newUser = new UserData("username", "password", "email@gmail.com");
        Assertions.assertThrows(Exception.class, () -> facade.login(newUser));
    }

    @Test
    public void logoutSuccess() throws Exception {
        UserData newUser = new UserData("username", "password", "email@gmail.com");
        AuthData auth = facade.register(newUser);
        Assertions.assertDoesNotThrow(() -> facade.logout(auth.authToken()));
    }

    @Test
    public void logoutFailure() {
        String auth = "randomAuth";
        Assertions.assertThrows(Exception.class, () -> facade.logout(auth));
    }

    @Test
    public void createSuccess() throws Exception {
        UserData newUser = new UserData("username", "password", "email@gmail.com");
        AuthData auth = facade.register(newUser);
        GameData game = new GameData(0, null, null, "CoolName", null);
        Assertions.assertDoesNotThrow(() -> facade.create(game, auth.authToken()));
    }

    @Test
    public void createFailure() throws Exception {
        UserData newUser = new UserData("username", "password", "email@gmail.com");
        AuthData auth = facade.register(newUser);
        GameData game = new GameData(0, null, null, null, null);
        Assertions.assertThrows(Exception.class, () -> facade.create(game, auth.authToken()));
    }

    @Test
    public void listSuccess() throws Exception {
        UserData newUser = new UserData("username", "password", "email@gmail.com");
        AuthData auth = facade.register(newUser);
        GameData game1 = new GameData(0, null, null, "CoolName1", null);
        GameData game2 = new GameData(0, null, null, "CoolName2", null);
        facade.create(game1, auth.authToken());
        facade.create(game2, auth.authToken());
        Collection<GameData> games = facade.list(auth.authToken());
        Assertions.assertEquals(games.size(), 2);
    }

    @Test
    public void listFailure() throws Exception {
        UserData newUser = new UserData("username", "password", "email@gmail.com");
        AuthData auth = facade.register(newUser);
        GameData game1 = new GameData(0, null, null, "CoolName1", null);
        GameData game2 = new GameData(0, null, null, "CoolName2", null);
        Collection<GameData> games = facade.list(auth.authToken());
        Assertions.assertEquals(games.size(), 0);
    }

    @Test
    public void joinSuccess() throws Exception {
        UserData newUser = new UserData("username", "password", "email@gmail.com");
        AuthData auth = facade.register(newUser);
        GameData game = new GameData(0, null, null, "CoolName", null);
        facade.create(game, auth.authToken());
        Assertions.assertDoesNotThrow(() -> facade.join(new JoinRequest(ChessGame.TeamColor.WHITE, 1), auth.authToken()));
    }

    @Test
    public void joinFailure() throws Exception {
        UserData newUser = new UserData("username", "password", "email@gmail.com");
        AuthData auth = facade.register(newUser);
        GameData game = new GameData(0, null, null, "CoolName", null);
        facade.create(game, auth.authToken());
        Assertions.assertThrows(Exception.class, () -> facade.join(new JoinRequest(ChessGame.TeamColor.WHITE, 2), auth.authToken()));
    }

    @Test
    public void clearSuccess() throws Exception {
        UserData newUser = new UserData("username", "password", "email@gmail.com");
        AuthData auth = facade.register(newUser);
        GameData game = new GameData(0, null, null, "CoolName", null);
        facade.create(game, auth.authToken());
        facade.clear();
        AuthData newAuth = facade.register(newUser);
        Collection<GameData> games = facade.list(newAuth.authToken());
        Assertions.assertEquals(games.size(), 0);
    }
}
