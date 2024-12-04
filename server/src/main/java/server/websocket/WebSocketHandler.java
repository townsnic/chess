package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.messages.*;
import websocket.commands.*;

import java.util.Objects;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final Gson gson = new Gson();
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public WebSocketHandler(GameDAO gameDAO, AuthDAO authDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
        MakeMoveCommand moveCommand = gson.fromJson(message, MakeMoveCommand.class);

        switch (command.getCommandType()) {
            case CONNECT -> connect(command, session);
            case MAKE_MOVE -> move(moveCommand, session);
            case LEAVE -> leave(command);
            case RESIGN -> resign(command);
        }
    }

    private void connect(UserGameCommand command, Session session) throws Exception {
        String authToken = command.getAuthToken();
        String username = authDAO.getAuth(authToken).username();
        int gameID = command.getGameID();
        GameData game = gameDAO.getGame(gameID);
        connections.addConnection(gameID, authToken, session);

        if (authDAO.getAuth(authToken) == null) {
            ErrorMessage error = new ErrorMessage("Error: Unauthorized");
            connections.sendToSelf(gameID, authToken, error);
            return;
        }
        if (gameDAO.getGame(gameID) == null) {
            ErrorMessage error = new ErrorMessage("Error: Invalid game ID");
            connections.sendToSelf(gameID, authToken, error);
            return;
        }

        String message;
        if (Objects.equals(username, game.whiteUsername())) {
            message = String.format("%s has joined %s as white.", username, gameDAO.getGame(gameID).gameName());
            gameDAO.updateGame(new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game()));
        } else if (Objects.equals(username, game.blackUsername())){
            message = String.format("%s has joined %s as black.", username, gameDAO.getGame(gameID).gameName());
            gameDAO.updateGame(new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game()));
        } else {
            message = String.format("%s is observing %s.", username, gameDAO.getGame(gameID).gameName());
        }

        NotificationMessage notification = new NotificationMessage(message);
        connections.broadcast(gameID, authToken, notification);
        connections.sendToSelf(gameID, authToken, new LoadGameMessage(gameDAO.getGame(gameID).game(), null));
    }

    private void move(MakeMoveCommand command, Session session) throws Exception {
        String authToken = command.getAuthToken();
        String username = authDAO.getAuth(authToken).username();
        int gameID = command.getGameID();
        GameData game = gameDAO.getGame(gameID);

        if (authDAO.getAuth(authToken) == null) {
            ErrorMessage error = new ErrorMessage("Error: Unauthorized");
            connections.addConnection(gameID, authToken, session);
            connections.sendToSelf(gameID, authToken, error);
            return;
        }
        if (game.game().gameOver) {
            ErrorMessage error = new ErrorMessage("Error: The game is over. No more moves can be made.");
            connections.sendToSelf(gameID, authToken, error);
            return;
        }
        if (!Objects.equals(username, game.blackUsername()) && !Objects.equals(username, game.whiteUsername())) {
            ErrorMessage error = new ErrorMessage("Error: An observer cannot make a move.");
            connections.sendToSelf(gameID, authToken, error);
            return;
        }
        if ((Objects.equals(username, game.blackUsername()) &&
                (game.game().getBoard().getPiece(command.getMove().getStartPosition()).getTeamColor() == ChessGame.TeamColor.WHITE)) ||
                (Objects.equals(username, game.whiteUsername()) &&
                        (game.game().getBoard().getPiece(command.getMove().getStartPosition()).getTeamColor() == ChessGame.TeamColor.BLACK))) {
            ErrorMessage error = new ErrorMessage("Error: You cannot move your opponent's piece.");
            connections.sendToSelf(gameID, authToken, error);
            return;
        }

        try {
            game.game().makeMove(command.getMove());
        } catch (Exception ex) {
            ErrorMessage error = new ErrorMessage(ex.getMessage());
            connections.sendToSelf(gameID, authToken, error);
            return;
        }

        String gameUpdate = null;
        String selfUpdate = null;
        if (game.game().isInCheck(ChessGame.TeamColor.BLACK)) {
            selfUpdate = gameUpdate = String.format("%s is in check.", game.blackUsername());
        } else if (game.game().isInCheck(ChessGame.TeamColor.WHITE)) {
            selfUpdate = gameUpdate = String.format("%s is in check.", game.whiteUsername());
        }

        if (game.game().isInCheckmate(ChessGame.TeamColor.BLACK)) {
            if (Objects.equals(username, game.blackUsername())) {
                selfUpdate = String.format("You are in checkmate. %s wins!", game.whiteUsername());
            } else {
                selfUpdate = String.format("%s is in checkmate. You win!", game.blackUsername());
            }
            gameUpdate = String.format("%s is in checkmate. %s wins!", game.blackUsername(), game.whiteUsername());
            game.game().gameOver = true;
        } else if (game.game().isInCheckmate(ChessGame.TeamColor.WHITE)) {
            if (Objects.equals(username, game.whiteUsername())) {
                selfUpdate = String.format("You are in checkmate. %s wins!", game.blackUsername());
            } else {
                selfUpdate = String.format("%s is in checkmate. You win!", game.whiteUsername());
            }
            gameUpdate = String.format("%s is in checkmate. %s wins!", game.whiteUsername(), game.blackUsername());
            game.game().gameOver = true;
        }

        if (game.game().isInStalemate(ChessGame.TeamColor.BLACK)) {
            selfUpdate = gameUpdate = String.format("%s is in stalemate. It's a draw.", game.blackUsername());
            game.game().gameOver = true;
        } else if (game.game().isInStalemate(ChessGame.TeamColor.WHITE)) {
            selfUpdate = gameUpdate = String.format("%s is in stalemate. It's a draw.", game.whiteUsername());
            game.game().gameOver = true;
        }

        gameDAO.updateGame(new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), game.game()));

        LoadGameMessage gameMessage = new LoadGameMessage(game.game(), command.getMove());
        connections.broadcast(gameID, authToken, gameMessage);
        connections.sendToSelf(gameID, authToken, gameMessage);

        String message = String.format("%s has made a move.", username);
        NotificationMessage notification = new NotificationMessage(message);
        connections.broadcast(gameID, authToken, notification);

        if (gameUpdate != null) {
            NotificationMessage gameNotify = new NotificationMessage(gameUpdate);
            NotificationMessage selfNotify = new NotificationMessage(selfUpdate);
            connections.broadcast(gameID, authToken, gameNotify);
            connections.sendToSelf(gameID, authToken, selfNotify);
        }
    }


    private void leave(UserGameCommand command) throws Exception {
        String authToken = command.getAuthToken();
        int gameID = command.getGameID();
        connections.removeConnection(gameID, authToken);
        String username = authDAO.getAuth(authToken).username();

        GameData oldGame = gameDAO.getGame(gameID);
        if (Objects.equals(username, oldGame.blackUsername())) {
            gameDAO.updateGame(new GameData(oldGame.gameID(), oldGame.whiteUsername(), null, oldGame.gameName(), oldGame.game()));
        } else if (Objects.equals(username, oldGame.whiteUsername())) {
            gameDAO.updateGame(new GameData(oldGame.gameID(), null, oldGame.blackUsername(), oldGame.gameName(), oldGame.game()));
        }

        String message = String.format("%s has left %s.", username, gameDAO.getGame(gameID).gameName());
        NotificationMessage notification = new NotificationMessage(message);
        connections.broadcast(gameID, authToken, notification);
    }

    private void resign(UserGameCommand command) throws Exception {
        String username = authDAO.getAuth(command.getAuthToken()).username();
        GameData oldGame = gameDAO.getGame(command.getGameID());
        ChessGame game = oldGame.game();

        if (game.gameOver) {
            ErrorMessage error = new ErrorMessage("Error: The game is already over.");
            connections.sendToSelf(oldGame.gameID(), command.getAuthToken(), error);
            return;
        }
        if (!Objects.equals(username, oldGame.blackUsername()) && !Objects.equals(username, oldGame.whiteUsername())) {
            ErrorMessage error = new ErrorMessage("Error: An observer cannot resign.");
            connections.sendToSelf(oldGame.gameID(), command.getAuthToken(), error);
            return;
        }

        game.gameOver = true;
        GameData newGame = new GameData(oldGame.gameID(), oldGame.whiteUsername(), oldGame.blackUsername(), oldGame.gameName(), game);
        gameDAO.updateGame(newGame);

        String message = String.format("%s has forfeited %s.", username, oldGame.gameName());
        String selfMessage = String.format("You have forfeited %s.", oldGame.gameName());
        NotificationMessage notification = new NotificationMessage(message);
        NotificationMessage selfNotification = new NotificationMessage(selfMessage);
        connections.broadcast(oldGame.gameID(), command.getAuthToken(), notification);
        connections.sendToSelf(oldGame.gameID(), command.getAuthToken(), selfNotification);
    }
}