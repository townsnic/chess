package server;

import com.google.gson.Gson;
import dataaccess.*;
import service.*;
import model.*;
import spark.*;

import java.util.Collection;
import java.util.Map;

public class Server {
    private final UserDAO userDAO = new MemoryUserDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final UserService userService = new UserService(userDAO, authDAO);
    private final GameService gameService = new GameService(gameDAO, authDAO);
    private final Gson serializer = new Gson();

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.post("/game", this::create);
        Spark.get("/game", this::list);
        Spark.put("/game", this::join);
        Spark.delete("/session", this::logout);
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
        UserData user = serializer.fromJson(req.body(), UserData.class);
        AuthData result = userService.loginUser(user);
        return serializer.toJson(result);
    }

    private String logout(Request req, Response res) throws Exception {
        String authToken = req.headers("authorization");
        userService.logoutUser(authToken);
        return "{}";
    }

    private String list(Request req, Response res) throws Exception {
        String authToken = req.headers("authorization");
        Collection<GameData> result = gameService.listGames(authToken);
        return serializer.toJson(Map.of("games", result));
    }

    private String create(Request req, Response res) throws Exception {
        String authToken = req.headers("authorization");
        GameData gameData = serializer.fromJson(req.body(), GameData.class);
        GameData result = gameService.createGame(authToken, gameData);
        return serializer.toJson(result);
    }

    private String join(Request req, Response res) throws Exception {
        String authToken = req.headers("authorization");
        JoinRequest joinRequest = serializer.fromJson(req.body(), JoinRequest.class);
        gameService.joinGame(authToken, joinRequest);
        return "{}";
    }

    private String clear(Request req, Response res) throws Exception {
        userService.clear();
        gameService.clear();
        return "{}";
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
