package common.models.message;

public class ServerMessage {
    private final String content;
    private final ServerStatus status;

    public ServerMessage(String content, ServerStatus status) {
        this.content = content;
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public ServerStatus getStatus() {
        return status;
    }
}
