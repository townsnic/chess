package server.websocket;

import com.google.gson.Gson;
import dataaccess.UserDAO;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.messages.ServerMessage;
import websocket.commands.UserGameCommand;

import java.io.IOException;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);

        Connection con = connections.getConnection(command.getAuthToken(), session);
        if (con != null) {
            switch (command.getCommandType()) {
                case CONNECT -> connect(con, message);
                case MAKE_MOVE -> move(con, message);
                case LEAVE -> leave(con, message);
                case RESIGN -> resign(con, message);
            }
        } else {
            throw new Exception("Unknown user.");
        }
    }

    private void connect(Connection con, String message) {

    }

    private void move(Connection con, String message) {

    }

    private void leave(Connection con, String message) {

    }

    private void resign(Connection con, String message) {

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