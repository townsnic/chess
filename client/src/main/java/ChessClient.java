import java.util.*;

import static ui.EscapeSequences.*;

import chess.*;
import com.google.gson.Gson;
import model.*;
import serverfacade.ServerFacade;
import websocket.ServerMessageObserver;
import websocket.WebSocketCommunicator;
import websocket.messages.*;

public class ChessClient implements ServerMessageObserver {
    private final Collection<ChessPosition> highlights = new ArrayList<>();
    private final Gson gson = new Gson();
    private final ServerFacade server;
    private final String serverUrl;
    private WebSocketCommunicator ws;
    private String username = null;
    private String authToken = null;
    private ChessGame.TeamColor teamColor = null;
    private GameData currentGame;
    public State state = State.LOGGED_OUT;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl, this);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        try {
            String[] tokens = input.split(" ");
            String cmd = tokens[0];
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if (state == State.IN_GAME) {
                updateGame();
                highlights.clear();
            }
            return switch (state) {
                case LOGGED_OUT -> switch (cmd) {
                    case "register" -> register(params);
                    case "login" -> login(params);
                    case "help" -> help(params);
                    case "quit" -> quit(params);
                    default -> throw new Exception("Invalid input. Enter 'help' for options.");
                };
                case LOGGED_IN -> switch (cmd) {
                    case "create" -> createGame(params);
                    case "list" -> listGames();
                    case "join" -> playGame(params);
                    case "observe" -> observeGame(params);
                    case "logout" -> logout(params);
                    case "help" -> help(params);
                    case "quit" -> quit(params);
                    default -> throw new Exception("Invalid input. Enter 'help' for options.");
                };
                case IN_GAME -> switch (cmd) {
                    case "redraw" -> drawBoard(currentGame.game(), teamColor) + "\n";
                    case "leave" -> leaveGame(params);
                    case "move" -> makeMove(params);
                    case "resign" -> resign(params);
                    case "highlight" -> highlight(params);
                    case "help" -> help(params);
                    default -> throw new Exception("Invalid input. Enter 'help' for options.");
                };
            };
        } catch (Exception ex) {
            return String.format(RESET_TEXT_COLOR + "\n" + "[" + state + "]" + " >>> " + SET_TEXT_COLOR_RED + ex.getMessage() + "\n");
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
        result.append(String.format("%-10s %-20s %-20s %-20s %-20s%n", "Game ID", "Game Name", "White Player", "Black Player", "Status"));
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
            String status = "Active";
            if (game.game().gameOver) {
                status = "Finished";
            }
            result.append(String.format("%-10s %-20s %-20s %-20s %-20s%n", gameNum, game.gameName(), whiteUsername, blackUsername, status));
            ++gameNum;
        }
        return result.toString();
    }

    public String observeGame(String... params) throws Exception {
        assertLoggedIn();
        if (params.length == 1) {
            int gameNum;
            try {
                gameNum = Integer.parseInt(params[0]);
            } catch (Exception ex) {
                throw new Exception("Error: Please provide a valid game ID.");
            }
            Collection<GameData> games = server.list(authToken);
            if (gameNum < 1 || gameNum > games.size()) {
                throw new Exception("Error: Please provide a valid game ID.");
            }
            ArrayList<GameData> gameList = new ArrayList<>(games);
            currentGame = gameList.get(gameNum - 1);
            teamColor = null;
            String successMessage = String.format("You are now observing %s.", currentGame.gameName());
            state = State.IN_GAME;
            ws = new WebSocketCommunicator(serverUrl, this);
            ws.joinGame(authToken, currentGame.gameID());
            return successMessage;
        }
        throw new Exception("Invalid Command. Expected: <ID>");
    }

    public String playGame(String... params) throws Exception {
        assertLoggedIn();
        if (params.length == 2) {
            int gameNum;
            try {
                gameNum = Integer.parseInt(params[0]);
            } catch (Exception ex) {
                throw new Exception("Error: Please provide a valid game ID.");
            }
            Collection<GameData> games = server.list(authToken);
            if (gameNum < 1 || gameNum > games.size()) {
                throw new Exception("Error: Please provide a valid game ID.");
            }
            ArrayList<GameData> gameList = new ArrayList<>(games);
            currentGame = gameList.get(gameNum - 1);
            String color = params[1];
            if (Objects.equals(color.toUpperCase(), "WHITE")) {
                teamColor = ChessGame.TeamColor.WHITE;
            } else if (Objects.equals(color.toUpperCase(), "BLACK")) {
                teamColor = ChessGame.TeamColor.BLACK;
            } else {
                throw new Exception("Error: Please select a valid team color.");
            }
            JoinRequest newRequest = new JoinRequest(teamColor, currentGame.gameID());
            server.join(newRequest, authToken);
            String successMessage = String.format("You are now playing %s as %s.", currentGame.gameName(), color.toLowerCase());
            state = State.IN_GAME;
            ws = new WebSocketCommunicator(serverUrl, this);
            ws.joinGame(authToken, currentGame.gameID());
            return successMessage;
        }
        throw new Exception("Invalid Command. Expected: <ID> <WHITE|BLACK>");
    }

    public String leaveGame(String... params) throws Exception {
        if (params.length == 0) {
            ws.leaveGame(authToken, currentGame.gameID());
            state = State.LOGGED_IN;
            String successMessage = String.format("You have left %s.", currentGame.gameName());
            currentGame = null;
            teamColor = null;
            return successMessage;
        }
        throw new Exception("Invalid Command. No parameters required.");
    }

    public String resign(String... params) throws Exception {
        if (params.length == 0) {
            System.out.printf(SET_TEXT_COLOR_BLUE + "Are you sure you want to forfeit %s?\n" + RESET_TEXT_COLOR + ">>> "
                    + SET_TEXT_COLOR_GREEN, currentGame.gameName());
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            if (line.equalsIgnoreCase("yes") || line.equalsIgnoreCase("y")) {
                ws.resign(authToken, currentGame.gameID());
                return String.format("You have forfeited %s.", currentGame.gameName());
            } else if (line.equalsIgnoreCase("no") || line.equalsIgnoreCase("n")) {
                return String.format("You are still playing %s.", currentGame.gameName());
            } else {
                throw new Exception("Invalid Input. Expected <YES|NO>");
            }
        }
        throw new Exception("Invalid Command. No parameters required.");
    }

    public String makeMove(String... params) throws Exception {
        if (teamColor == null) {
            throw new Exception("Error: An observer cannot make a move.");
        }
        if (!currentGame.game().gameOver) {
            if (params.length == 2) {
                String curPos = params[0];
                String newPos = params[1];
                if (curPos.length() != 2 || newPos.length() != 2) {
                    throw new Exception("Error: Please provide a valid move.");
                }
                ChessPosition startPos = getChessPosition(curPos);
                ChessPosition endPos = getChessPosition(newPos);
                ChessPiece curPiece = currentGame.game().getBoard().getPiece(startPos);
                int endRow = Integer.parseInt(Character.toString(newPos.charAt(1)));
                if (curPiece == null) {
                    throw new Exception("Error: There is no piece at that position.");
                }
                if (curPiece.getTeamColor() != teamColor) {
                    throw new Exception("Error: You cannot move your opponent's piece.");
                }
                ChessPiece.PieceType promotionPiece = null;
                if ((curPiece.getPieceType() == chess.ChessPiece.PieceType.PAWN) &&
                        ((curPiece.getTeamColor() == ChessGame.TeamColor.WHITE && endRow == 8) ||
                                (curPiece.getTeamColor() == ChessGame.TeamColor.BLACK && endRow == 1))) {
                    System.out.print(SET_TEXT_COLOR_BLUE + "What would you like to promote your pawn to?\n" +
                            RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
                    Scanner scanner = new Scanner(System.in);
                    String line = scanner.nextLine();
                    promotionPiece = switch (line.toLowerCase()) {
                        case "queen" -> ChessPiece.PieceType.QUEEN;
                        case "rook" -> ChessPiece.PieceType.ROOK;
                        case "bishop" -> ChessPiece.PieceType.BISHOP;
                        case "knight" -> ChessPiece.PieceType.KNIGHT;
                        default -> throw new Exception("Error: Please provide a valid piece.");
                    };
                }
                ChessMove move = new ChessMove(startPos, endPos, promotionPiece);
                ws.move(authToken, currentGame.gameID(), move);
                return "";
            }
            throw new Exception("Invalid Command. Expected: <CURRENT_SPACE> <NEW_SPACE>");
        }
        throw new Exception("Error: The game is over. No more moves can be made.");
    }

    public String drawBoard(ChessGame game, ChessGame.TeamColor perspective) {
        ChessBoard board = game.getBoard();
        StringBuilder printBoard = new StringBuilder();
        printBoard.append("\n");
        String[] columns;
        if (perspective == ChessGame.TeamColor.BLACK) {
            columns = new String[] {"h", "g", "f", "e", "d", "c", "b", "a"};
        } else {
            columns = new String[] {"a", "b", "c", "d", "e", "f", "g", "h"};
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
        int startCol;
        int rowIncrement;
        int colIncrement;
        if (perspective == ChessGame.TeamColor.BLACK) {
            startRow = 1;
            rowIncrement = 1;
            startCol = 8;
            colIncrement = -1;
        } else {
            startRow = 8;
            rowIncrement = -1;
            startCol = 1;
            colIncrement = 1;
        }
        for (int row = startRow; (perspective == ChessGame.TeamColor.BLACK) ? row < 9 : row > 0; row += rowIncrement) {
            int space = (row % 2) + 1;
            printBoard.append(SET_BG_COLOR_BLUE).append(SET_TEXT_COLOR_BLACK).append("\u2003").append(row).append("\u2003");

            for (int col = startCol; (perspective == ChessGame.TeamColor.BLACK) ? col > 0 : col < 9; col += colIncrement) {
                ChessPosition curPos = new ChessPosition(row, col);
                if (space % 2 == ((perspective == ChessGame.TeamColor.BLACK) ? 0 : 1)) {
                    if (highlights.contains(curPos)) {
                        printBoard.append(SET_BG_COLOR_GREEN);
                    } else {
                        printBoard.append(SET_BG_COLOR_LIGHT_GREY);
                    }
                } else {
                    if (highlights.contains(curPos)) {
                        printBoard.append(SET_BG_COLOR_DARK_GREEN);
                    } else {
                        printBoard.append(SET_BG_COLOR_DARK_GREY);
                    }
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
        printBoard.append("\u2003\u2009").append(EMPTY).append(RESET_BG_COLOR);
        highlights.clear();
        return printBoard.toString();
    }

    public String highlight(String... params) throws Exception {
        if (params.length == 1) {
            String position = params[0];
            if (position.length() != 2) {
                throw new Exception("Error: Please provide a valid position.");
            }
            ChessPosition myPosition = getChessPosition(position);
            Collection<ChessMove> validMoves = currentGame.game().validMoves(myPosition);
            for (ChessMove move : validMoves) {
                highlights.add(move.getEndPosition());
            }
            return drawBoard(currentGame.game(), teamColor) + "\n";
        }
        throw new Exception("Invalid Command. Expected: <SPACE>");
    }

    private static ChessPosition getChessPosition(String position) throws Exception {
        int col = switch (position.charAt(0)) {
            case 'a' -> 1;
            case 'b' -> 2;
            case 'c' -> 3;
            case 'd' -> 4;
            case 'e' -> 5;
            case 'f' -> 6;
            case 'g' -> 7;
            case 'h' -> 8;
            default -> throw new Exception("Error: Please provide a valid position.");
        };
        int row = Integer.parseInt(Character.toString(position.charAt(1)));
        if (row < 1 || row > 8) {
            throw new Exception("Error: Please provide a valid position.");
        }
        return new ChessPosition(row, col);
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
                        - move <CURRENT_SPACE> <NEW_SPACE>
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

    private void updateGame() throws Exception {
        Collection<GameData> games = server.list(authToken);
        ArrayList<GameData> gameList = new ArrayList<>(games);
        int gameID = currentGame.gameID();
        currentGame = gameList.get(gameID - 1);
    }

    private String loadGame(String json) {
        LoadGameMessage message = gson.fromJson(json, LoadGameMessage.class);
        if (message.getMove() != null) {
            highlights.add(message.getMove().getStartPosition());
            highlights.add(message.getMove().getEndPosition());
        }
        return drawBoard(message.getGame(), teamColor);
    }

    @Override
    public void notify(String json) {
        ServerMessage message = gson.fromJson(json, ServerMessage.class);
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> System.out.printf(SET_TEXT_COLOR_BLUE + gson.fromJson(json, NotificationMessage.class).getMessage());
            case ERROR -> System.out.printf(SET_TEXT_COLOR_RED + gson.fromJson(json, ErrorMessage.class).getErrorMessage() + "\n");
            case LOAD_GAME -> System.out.println("\n" + loadGame(json));
        }
        System.out.print(RESET_BG_COLOR + RESET_TEXT_COLOR + "\n" + "[" + state + "]" + " >>> " + SET_TEXT_COLOR_GREEN);
    }
}