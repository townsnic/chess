import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import static ui.EscapeSequences.*;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.*;
import serverfacade.ServerFacade;
import websocket.ServerMessageObserver;
import websocket.WebSocketCommunicator;
import websocket.messages.*;

public class ChessClient implements ServerMessageObserver {
    private final ServerFacade server;
    private final String serverUrl;
    private WebSocketCommunicator ws;
    private String username = null;
    private String authToken = null;
    public State state = State.LOGGED_OUT;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl, this);
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
                    case "quit" -> quit(params);
                    default -> "Invalid input. Enter 'help' for options.";
                };
                case LOGGED_IN -> switch (cmd) {
                    case "create" -> createGame(params);
                    case "list" -> listGames();
                    case "join" -> playGame(params);
                    case "observe" -> observeGame(params);
                    case "logout" -> logout(params);
                    case "help" -> help(params);
                    case "quit" -> quit(params);
                    default -> "Invalid input. Enter 'help' for options.";
                };
                case IN_GAME -> switch (cmd) {
                    case "redraw" -> "redraw"; //drawBoard(game, color);
                    case "leave" -> "leave";
                    case "move" -> "move";
                    case "resign" -> "resign";
                    case "highlight" -> "highlight";
                    case "help" -> help(params);
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
        result.append(String.format("%-10s %-20s %-20s %-20s%n", "Game ID", "Game Name", "White Player", "Black Player"));
        for (GameData game : games) {
            String whiteUsername;
            String blackUsername;
            if (game.whiteUsername() == null) {
                whiteUsername = "";
            } else {
                whiteUsername = game.whiteUsername();
            }
            if (game.blackUsername() == null) {
                blackUsername = "";
            } else {
                blackUsername = game.blackUsername();
            }
            result.append(String.format("%-10s %-20s %-20s %-20s%n", gameNum, game.gameName(), whiteUsername, blackUsername));
            ++gameNum;
        }
        return result.toString();
    }

    public String observeGame(String... params) throws Exception {
        assertLoggedIn();
        if (params.length == 1) {
            int gameNum = Integer.parseInt(params[0]);
            Collection<GameData> games = server.list(authToken);
            if (gameNum < 1 || gameNum > games.size()) {
                throw new Exception("Please provide a valid game ID.");
            }
            ArrayList<GameData> gameList = new ArrayList<>(games);
            GameData correctGame = gameList.get(gameNum - 1);

            String successMessage = String.format("You are now observing %s.", correctGame.gameName());
            String whiteBoard = drawBoard(correctGame.game(), ChessGame.TeamColor.WHITE);
            String blackBoard = drawBoard(correctGame.game(), ChessGame.TeamColor.BLACK);
            return successMessage + "\n" + whiteBoard + "\n" + blackBoard;
        }
        throw new Exception("Invalid Command. Expected: <ID>");
    }

    public String playGame(String... params) throws Exception {
        assertLoggedIn();
        if (params.length == 2) {
            int gameNum = Integer.parseInt(params[0]);
            Collection<GameData> games = server.list(authToken);
            if (gameNum < 1 || gameNum > games.size()) {
                throw new Exception("Please provide a valid game ID.");
            }
            ArrayList<GameData> gameList = new ArrayList<>(games);
            GameData correctGame = gameList.get(gameNum - 1);

            String color = params[1];
            ChessGame.TeamColor teamColor;
            if (Objects.equals(color.toUpperCase(), "WHITE")) {
                teamColor = ChessGame.TeamColor.WHITE;
            } else if (Objects.equals(color.toUpperCase(), "BLACK")) {
                teamColor = ChessGame.TeamColor.BLACK;
            } else {
                throw new Exception("Please select a valid team color.");
            }

            JoinRequest newRequest = new JoinRequest(teamColor, correctGame.gameID());
            server.join(newRequest, authToken);
            String successMessage = String.format("You are now playing %s as %s.", correctGame.gameName(), color.toLowerCase());
            ws = new WebSocketCommunicator(serverUrl, this);
            ws.joinGame(authToken, gameNum - 1);
            state = State.IN_GAME;
            String board = drawBoard(correctGame.game(), teamColor);
            return successMessage + "\n" + board;
        }
        throw new Exception("Invalid Command. Expected: <ID> <WHITE|BLACK>");
    }

    public String drawBoard(ChessGame game, ChessGame.TeamColor perspective) {
        ChessBoard board = game.getBoard();
        StringBuilder printBoard = new StringBuilder();
        String[] columns;
        if (perspective == ChessGame.TeamColor.WHITE) {
            columns = new String[] {"a", "b", "c", "d", "e", "f", "g", "h"};
        } else {
            columns = new String[] {"h", "g", "f", "e", "d", "c", "b", "a"};
        }

        printBoard.append(SET_BG_COLOR_BLUE).append(SET_TEXT_COLOR_BLACK).append(EMPTY).append("\u2009");
        for (int col = 0; col < columns.length; ++col) {
            printBoard.append("\u2003").append(columns[col]);
            if (col < 7) {
                printBoard.append(" ");
            }
        }
        printBoard.append("\u2003\u2009").append(EMPTY).append(RESET_BG_COLOR).append("\n");

        int startRow;
        int rowIncrement;
        if (perspective == ChessGame.TeamColor.WHITE) {
            startRow = 8;
            rowIncrement = -1;
        } else {
            startRow = 1;
            rowIncrement = 1;
        }

        for (int row = startRow; (perspective == ChessGame.TeamColor.WHITE) ? row > 0 : row < 9; row += rowIncrement) {
            int space = (row % 2) + 1;
            printBoard.append(SET_BG_COLOR_BLUE).append(SET_TEXT_COLOR_BLACK).append("\u2003").append(row).append("\u2003");
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
            printBoard.append(SET_BG_COLOR_BLUE).append(SET_TEXT_COLOR_BLACK).append("\u2003").append(row).append("\u2003");
            printBoard.append(RESET_BG_COLOR).append("\n");
        }

        printBoard.append(SET_BG_COLOR_BLUE).append(EMPTY).append("\u2009");
        for (int col = 0; col < columns.length; ++col) {
            printBoard.append("\u2003").append(columns[col]);
            if (col < 7) {
                printBoard.append(" ");
            }
        }
        printBoard.append("\u2003\u2009").append(EMPTY).append(RESET_BG_COLOR).append("\n");

        return printBoard.toString();
    }

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
            } else if (state == State.IN_GAME) {
                return """
                        - redraw
                        - move <MOVE>
                        - highlight <SPACE>
                        - leave
                        - resign
                        - help
                        """;
            }
            return null;
        }
        throw new Exception("Invalid Command. No parameters required.");
    }

    public String quit(String... params) throws Exception {
        if (params.length == 0) {
            return "Leaving Chess Arena. Come back soon!";
        }
        throw new Exception("Invalid Command. No parameters required.");
    }

    private void assertLoggedIn() throws Exception {
        if (state == State.LOGGED_OUT) {
            throw new Exception("You are not logged in.");
        }
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> System.out.println("Notification");//displayNotification(((NotificationMessage) message).getMessage());
            case ERROR -> System.out.println("error");//displayError(((ErrorMessage) message).getErrorMessage());
            case LOAD_GAME -> System.out.println("Game");//loadGame(((LoadGameMessage) message).getGame());
        }
    }

}