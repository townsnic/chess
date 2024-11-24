package websocket.messages;

public class ErrorMessage extends ServerMessage {

    private final String errorMessage;

    public ErrorMessage(ServerMessageType messageType, String errorMessage) {
        super(messageType);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
