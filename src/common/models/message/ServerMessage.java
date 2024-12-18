package common.models.message;

public class ServerMessage {
    private final String content;
    private final int status;  // 0 OK, 1 ERROR, 2 INFO

    public ServerMessage(String content, int status) {
        this.content = content;
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public ServerStatus getStatus() {
        return ServerStatus.fromValue(status);
    }

    // Enumerar los posibles estados para mayor claridad
    public enum ServerStatus {
        LOGIN_OK(0), ERROR(1), INFO(2), DISCONNECT(3);

        private final int value;

        ServerStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static ServerStatus fromValue(int value) {
            for (ServerStatus status : ServerStatus.values()) {
                if (status.value == value) {
                    return status;
                }
            }
            return null;
        }
    }
}