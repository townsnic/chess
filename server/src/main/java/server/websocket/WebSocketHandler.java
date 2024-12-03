package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.UserDAO;
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
    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public WebSocketHandler(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
        MakeMoveCommand moveCommand = gson.fromJson(message, MakeMoveCommand.class);

        switch (command.getCommandType()) {
            case CONNECT -> connect(command, session);
            case MAKE_MOVE -> move(moveCommand);
            case LEAVE -> leave(command);
            case RESIGN -> resign(command);
        }
    }

    private void connect(UserGameCommand command, Session session) throws Exception {
        String authToken = command.getAuthToken();
        int gameID = command.getGameID();
        connections.addConnection(gameID, authToken, session);

        if (authDAO.getAuth(authToken) == null) {
            ErrorMessage error = new ErrorMessage("Error: Unauthorized");
            connections.sendToSelf(gameID, authToken, error);
            return;
        }

        String username = authDAO.getAuth(authToken).username();
        String message;

        if (gameDAO.getGame(gameID) == null) {
            ErrorMessage error = new ErrorMessage("Error: Invalid game ID");
            connections.sendToSelf(gameID, authToken, error);
            return;
        }

        if (Objects.equals(username, gameDAO.getGame(gameID).whiteUsername())) {
            message = String.format("%s has joined %s as white.", username, gameDAO.getGame(gameID).gameName());
        } else if (Objects.equals(username, gameDAO.getGame(gameID).blackUsername())){
            message = String.format("%s has joined %s as black.", username, gameDAO.getGame(gameID).gameName());
        } else {
            message = String.format("%s is observing %s.", username, gameDAO.getGame(gameID).gameName());
        }
        NotificationMessage notification = new NotificationMessage(message);
        connections.broadcast(gameID, authToken, notification);
        connections.sendToSelf(gameID, authToken, new LoadGameMessage(gameDAO.getGame(gameID).game()));
    }

    private void move(MakeMoveCommand command) throws Exception {
        String authToken = command.getAuthToken();
        int gameID = command.getGameID();
        String username = authDAO.getAuth(authToken).username();

        GameData game = gameDAO.getGame(gameID);
        game.game().makeMove(command.getMove());
        gameDAO.updateGame(new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), game.game()));

        LoadGameMessage gameMessage = new LoadGameMessage(game.game());
        connections.broadcast(gameID, authToken, gameMessage);
        connections.sendToSelf(gameID, authToken, gameMessage);

        String message = String.format("%s has made a move.", username);
        NotificationMessage notification = new NotificationMessage(message);
        connections.broadcast(gameID, authToken, notification);
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
        game.gameOver = true;
        GameData newGame = new GameData(oldGame.gameID(), oldGame.whiteUsername(), oldGame.blackUsername(), oldGame.gameName(), game);
        gameDAO.updateGame(newGame);

        String message = String.format("%s has forfeited %s.", username, oldGame.gameName());
        NotificationMessage notification = new NotificationMessage(message);
        connections.broadcast(oldGame.gameID(), command.getAuthToken(), notification);
    }

//    private void enter(String visitorName, Session session) throws IOException {
//        connections.add(visitorName, session);
//        var message = String.format("%s is in the shop", visitorName);
//        var notification = new Notification(Notification.Type.ARRIVAL, message);
//        connections.broadcast(visitorName, notification);
//    }
//
//    private void exit(String visitorName) throws IOException {
//        connections.remove(visitorName);
//        var message = String.format("%s left the shop", visitorName);
//        var notification = new Notification(Notification.Type.DEPARTURE, message);
//        connections.broadcast(visitorName, notification);
//    }
//
//    public void makeNoise(String petName, String sound) throws ResponseException {
//        try {
//            var message = String.format("%s says %s", petName, sound);
//            var notification = new Notification(Notification.Type.NOISE, message);
//            connections.broadcast("", notification);
//        } catch (Exception ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }
}