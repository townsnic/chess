import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static ui.EscapeSequences.*;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.*;

public class ChessClient {
    private final ServerFacade server;
    private final String serverUrl;
    private String username = null;
    private String authToken = null;
    public State state = State.LOGGED_OUT;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        try {
            if (input.isBlank()) {
                return "Invalid input. Enter 'help' for options.";
            }
            String[] tokens = input.split(" ");
            String cmd = tokens[0];
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
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
                    case "observe" -> observeGame(params);
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
            AuthData auth = server.register(newUser);
            authToken = auth.authToken();
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
            authToken = auth.authToken();
            state = State.LOGGED_IN;
            return String.format("You successfully logged in as %s.", username);
        }
        throw new Exception("Invalid Command. Expected: <USERNAME> <PASSWORD>");
    }

    public String logout(String... params) throws Exception {
        if (params.length == 0) {
            assertLoggedIn();
            server.logout(authToken);
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
            GameData createdGame = server.create(newGame, authToken);
            return String.format("You created %s.", createdGame.gameName());
        }
        throw new Exception("Invalid Command. Expected: <NAME>");
    }

    public String listGames() throws Exception {
        assertLoggedIn();
        Collection<GameData> games = server.list(authToken);
        StringBuilder result = new StringBuilder();
        int gameNum = 1;
        result.append(String.format("%-10s %-50s%n", "Game ID", "Game Name"));
        for (GameData game : games) {
            result.append(String.format("%-10s %-50s%n", gameNum, game.gameName()));
            ++gameNum;
        }
        return result.toString();
    }

    public String observeGame(String... params) throws Exception {
        assertLoggedIn();
        if (params.length == 1) {
            int gameNum = Integer.parseInt(params[0]);
            Collection<GameData> games = server.list(authToken);
            ArrayList<GameData> gameList = new ArrayList<>(games);
            GameData correctGame = gameList.get(gameNum);
            String successMessage = String.format("You are now observing %s.", correctGame.gameName());
            String board = drawBoardWhite(correctGame.game());
            return successMessage + "\n" + board;
        }
        throw new Exception("Invalid Command. Expected: <ID>");
    }

    public String playGame(String... params) throws Exception {
        assertLoggedIn();
        return "";
    }

    public String drawBoardWhite(ChessGame game) {
        ChessBoard board = game.getBoard();
        StringBuilder printBoard = new StringBuilder();
        printBoard.append(SET_BG_COLOR_BLUE).append(SET_TEXT_COLOR_BLACK);
        printBoard.append(EMPTY).append("\u2003\u2009a ").append("\u2003b ").append("\u2003c ");
        printBoard.append("\u2003d ").append("\u2003e ").append("\u2003f ").append("\u2003g ");
        printBoard.append("\u2003h \u2009\u2009\u2009").append(EMPTY).append(RESET_BG_COLOR).append("\n");

        for (int row = 8; row > 0; row--) {
            int space = (row % 2) + 1;
            printBoard.append(SET_TEXT_COLOR_BLACK).append(SET_BG_COLOR_BLUE).append("\u2003").append(row).append("\u2003");
            for (int col = 1; col < 9; col++) {
                if (space % 2 == 1) {
                    printBoard.append(SET_BG_COLOR_LIGHT_GREY);
                } else {
                    printBoard.append(SET_BG_COLOR_DARK_GREY);
                }
                ChessPiece currentPiece = board.getPiece(new ChessPosition(row, col));
                if (currentPiece != null) {
                    switch (currentPiece.getTeamColor()) {
                        case WHITE:
                            printBoard.append(SET_TEXT_COLOR_WHITE);
                            break;
                        case BLACK:
                            printBoard.append(SET_TEXT_COLOR_BLACK);
                    }
                    switch (currentPiece.getPieceType()) {
                        case PAWN:
                            printBoard.append(BLACK_PAWN);
                            break;
                        case KNIGHT:
                            printBoard.append(BLACK_KNIGHT);
                            break;
                        case BISHOP:
                            printBoard.append(BLACK_BISHOP);
                            break;
                        case ROOK:
                            printBoard.append(BLACK_ROOK);
                            break;
                        case QUEEN:
                            printBoard.append(BLACK_QUEEN);
                            break;
                        case KING:
                            printBoard.append(BLACK_KING);
                            break;
                    }
                } else {
                    printBoard.append(EMPTY);
                }
                space++;
            }
            printBoard.append(SET_TEXT_COLOR_BLACK).append(SET_BG_COLOR_BLUE).append("\u2003").append(row).append("\u2003").append(RESET_BG_COLOR).append("\n");
        }
        printBoard.append(SET_BG_COLOR_BLUE).append(EMPTY).append("\u2003\u2009a ").append("\u2003b ").append("\u2003c ");
        printBoard.append("\u2003d ").append("\u2003e ").append("\u2003f ").append("\u2003g ");
        printBoard.append("\u2003h \u2009\u2009\u2009").append(EMPTY).append(RESET_BG_COLOR).append("\n");
        return printBoard.toString();
    }

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