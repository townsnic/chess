package websocket.messages;

public class Notification extends ServerMessage {

    private final String message;

    public Notification(ServerMessageType messageType, String message) {
        super(messageType);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
