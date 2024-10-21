package server;

import com.google.gson.Gson;
import dataaccess.*;
import service.*;
import model.*;
import spark.*;

import java.util.Map;

public class Server {
    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();
    private final UserService userService = new UserService(userDAO, authDAO);
    private final AuthService authService = new AuthService(authDAO);
    private final GameService gameService = new GameService(gameDAO);
    private final Gson serializer = new Gson();

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::list);
        Spark.delete("/db", this::clear);
        Spark.exception(Exception.class, this::exceptionHandler);
        Spark.exception(ServiceException.class, this::serviceExceptionHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private String register(Request req, Response res) throws Exception {
        UserData newUser = serializer.fromJson(req.body(), UserData.class);
        AuthData result = userService.registerUser(newUser);
        return serializer.toJson(result);
    }

    private String login(Request req, Response res) throws Exception {
        UserData newUser = serializer.fromJson(req.body(), UserData.class);
        AuthData result = userService.loginUser(newUser);
        return serializer.toJson(result);
    }

    private String logout(Request req, Response res) throws Exception {
        AuthData userAuth = serializer.fromJson(req.body(), AuthData.class);
        //userService.logoutUser(userAuth);
        return serializer.toJson("");
    }

    private String list(Request req, Response res) throws Exception {
        AuthData userAuth = serializer.fromJson(req.body(), AuthData.class);
        // Add game data array to return
        return serializer.toJson("");
    }

    private String clear(Request req, Response res) throws Exception {
        userService.clear();
        authService.clear();
        gameService.clear();
        res.status(200);
        return "";
    }

    private void exceptionHandler(Exception ex, Request req, Response res) {
        res.status(500);
        res.body(serializer.toJson(Map.of("message", ex.getMessage())));
        ex.printStackTrace(System.out);
    }

    private void serviceExceptionHandler(ServiceException ex, Request req, Response res) {
        res.status(ex.StatusCode());
        res.body(serializer.toJson(Map.of("message", ex.getMessage())));
        ex.printStackTrace(System.out);
    }
}
