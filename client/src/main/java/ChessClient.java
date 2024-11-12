import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static ui.EscapeSequences.*;
import model.*;

public class ChessClient {
    private final ServerFacade server;
    private final String serverUrl;
    private String username = null;
    private String loginAuthToken = null;
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
//                    case "join" -> playGame(params);
//                    case "observe" -> observeGame(params);
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
            return String.format("You created %s.", createdGame.gameName());
        }
        throw new Exception("Invalid Command. Expected: <NAME>");
    }

    public String listGames() throws Exception {
        assertLoggedIn();
        Collection<GameData> games = server.list(loginAuthToken);
        StringBuilder result = new StringBuilder();
        int gameNum = 1;
        result.append(String.format("%-10s %-50s%n", "Game ID", "Game Name"));
        for (GameData game : games) {
            result.append(String.format("%-10s %-50s%n", gameNum, game.gameName()));
            ++gameNum;
        }
        return result.toString();
    }

//    public String observeGame(String... params) throws Exception {
//        assertLoggedIn();
//        if (params.length == 1) {
//            //int gameNum = params[0];
//            Collection<GameData> games = server.list(loginAuthToken);
//            ArrayList<GameData> gameList = new ArrayList<>(games);
//            //GameData correctGame = gameList.get(gameNum);
//            //return String.format("You are now observing %s.", correctGame.gameName());
//        }
//        throw new Exception("Invalid Command. Expected: <ID>");
//    }
//
//    public String playGame(String... params) throws Exception {
//        assertLoggedIn();
//        return "";
//    }

//    public String drawBoardWhite() {
//
//        StringBuilder board = new StringBuilder();
//        board.append(SET_BG_COLOR_BLUE).append(SET_TEXT_COLOR_BLACK);
//        board.append(EMPTY).append(" a  b  c  d  e  f  g  h ").append(EMPTY);
//        board.append("\n");
//        board.append(EMPTY);
//
//        for (int i = 0; i < 8; i++) {
//
//        }
//
//        return board.toString();
//    }
//
//    public String drawBoardBlack() {
//
//        StringBuilder board = new StringBuilder();
//        board.append(SET_BG_COLOR_BLUE).append(SET_TEXT_COLOR_BLACK);
//        board.append(EMPTY).append(" a  b  c  d  e  f  g  h ").append(EMPTY);
//        board.append("\n");
//        board.append(EMPTY);
//
//        for (int i = 0; i < 8; i++) {
//
//        }
//
//        return board.toString();
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