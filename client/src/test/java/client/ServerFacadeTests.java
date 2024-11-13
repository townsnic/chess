package client;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import serverfacade.ServerFacade;


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

}
