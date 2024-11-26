package server.websocket;

import com.google.gson.Gson;
import dataaccess.UserDAO;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.messages.*;
import websocket.commands.UserGameCommand;

import java.util.Objects;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
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
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command, session);
            case MAKE_MOVE -> move(command, message);
            case LEAVE -> leave(command, message);
            case RESIGN -> resign(command, message);
        }
    }

    private void connect(UserGameCommand command, Session session) throws Exception {
        String authToken = command.getAuthToken();
        int gameID = command.getGameID();
        connections.addConnection(gameID, authToken, session);
        String username = authDAO.getAuth(authToken).username();
        String message;
        if (Objects.equals(username, gameDAO.getGame(gameID).whiteUsername())) {
            message = String.format("%s has joined the game as white.", username);
        } else if (Objects.equals(username, gameDAO.getGame(gameID).blackUsername())){
            message = String.format("%s has joined the game as black.", username);
        } else {
            message = String.format("%s is observing the game.", username);
        }
        NotificationMessage notification = new NotificationMessage(message);
        connections.broadcast(gameID, authToken, notification);
        //connections.sendToSelf(authToken, new LoadGameMessage(new ChessGame()));
    }

    private void move(UserGameCommand command, String message) {

    }

    private void leave(UserGameCommand command, String message) {

    }

    private void resign(UserGameCommand command, String message) {

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