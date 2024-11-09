import java.util.Arrays;
import java.util.Collection;

import com.google.gson.Gson;
import model.*;

public class ChessClient {
    private String username = null;
    private String loginAuthToken = null;
    private final ServerFacade server;
    private final String serverUrl;
    public State state = State.LOGGED_OUT;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        try {
            var tokens = input.split(" ");
            var cmd = tokens[0];
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (state) {
                case LOGGED_OUT -> switch (cmd) {
                    case "register" -> register(params);
                    case "login" -> login(params);
                    case "help" -> help(params);
                    case "quit" -> "Leaving Chess Arena. Come back soon!";
                    default -> "Invalid input. Enter 'help' for options.";
                };
                case LOGGED_IN -> switch (cmd) {
                    case "create" -> createGame(params);
                    case "list" -> listGames();
//                    case "join" -> "joinGame()";
//                    case "observe" -> "observeGame()";
                    case "logout" -> logout(params);
                    case "help" -> help(params);
                    case "quit" -> "Leaving Chess Arena. Come back soon!";
                    default -> "Invalid input. Enter 'help' for options.";
                };
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws Exception {
        if (params.length == 3) {
            username = params[0];
            String password = params[1];
            String email = params[2];
            UserData newUser = new UserData(username, password, email);
            server.register(newUser);
            state = State.LOGGED_IN;
            return String.format("You successfully registered as %s.", username);
        }
        throw new Exception("Invalid Command. Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String login(String... params) throws Exception {
        if (params.length == 2) {
            username = params[0];
            String password = params[1];
            UserData user = new UserData(username, password, null);
            AuthData auth = server.login(user);
            loginAuthToken = auth.authToken();
            state = State.LOGGED_IN;
            return String.format("You successfully logged in as %s.", username);
        }
        throw new Exception("Invalid Command. Expected: <USERNAME> <PASSWORD>");
    }

    public String logout(String... params) throws Exception {
        if (params.length == 0) {
            assertLoggedIn();
            server.logout(loginAuthToken);
            state = State.LOGGED_OUT;
            return String.format("You have successfully logged out of account %s.", username);
        }
        throw new Exception("Invalid Command. No parameters required.");
    }

    public String createGame(String... params) throws Exception {
        assertLoggedIn();
        if (params.length == 1) {
            String gameName = params[0];
            GameData newGame = new GameData(0, null, null, gameName, null);
            GameData createdGame = server.create(newGame, loginAuthToken);
            return String.format("You created %s with ID: %d.", createdGame.gameName(), createdGame.gameID());
        }
        throw new Exception("Invalid Command. Expected: <NAME>");
    }

    public String listGames() throws Exception {
        assertLoggedIn();
        Collection<GameData> games = server.list(loginAuthToken);
        StringBuilder result = new StringBuilder();
        Gson gson = new Gson();
        for (GameData game : games) {
            result.append(gson.toJson(game)).append('\n');
        }
        return result.toString();
    }

//    public String adoptPet(String... params) throws ResponseException {
//        assertSignedIn();
//        if (params.length == 1) {
//            try {
//                var id = Integer.parseInt(params[0]);
//                var pet = getPet(id);
//                if (pet != null) {
//                    server.deletePet(id);
//                    return String.format("%s says %s", pet.name(), pet.sound());
//                }
//            } catch (NumberFormatException ignored) {
//            }
//        }
//        throw new ResponseException(400, "Expected: <pet id>");
//    }

//    public String adoptAllPets() throws ResponseException {
//        assertSignedIn();
//        var buffer = new StringBuilder();
//        for (var pet : server.listPets()) {
//            buffer.append(String.format("%s says %s%n", pet.name(), pet.sound()));
//        }
//
//        server.deleteAllPets();
//        return buffer.toString();
//    }

//    private Pet getPet(int id) throws ResponseException {
//        for (var pet : server.listPets()) {
//            if (pet.id() == id) {
//                return pet;
//            }
//        }
//        return null;
//    }

    public String help(String... params) throws Exception {
        if (params.length == 0) {
            if (state == State.LOGGED_OUT) {
                return """
                        - register <USERNAME> <PASSWORD> <EMAIL>
                        - login <USERNAME> <PASSWORD>
                        - quit
                        - help
                        """;
            } else if (state == State.LOGGED_IN) {
                return """
                        - create <NAME>
                        - list
                        - join <ID> <WHITE|BLACK>
                        - observe <ID>
                        - logout
                        - quit
                        - help
                        """;
            }
            return null;
        }
        throw new Exception("Invalid Command. No parameters required.");
    }

    private void assertLoggedIn() throws Exception {
        if (state == State.LOGGED_OUT) {
            throw new Exception("You are not logged in.");
        }
    }
}