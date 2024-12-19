package server.model.message;

/**
 * The ServerMessage class represents a message sent from the server to the client.
 * It contains the content of the message and the status of the server, which indicates
 * the type of message being sent (e.g., successful login, error, informational message, etc.).
 */
public class ServerMessage {
    private final String content;  // The content of the message
    private final int status;  // The status code indicating the type of message

    /**
     * Constructor for creating a new ServerMessage instance.
     *
     * @param content The content of the message.
     * @param status The status code of the message indicating its type.
     */
    public ServerMessage(String content, int status) {
        this.content = content;
        this.status = status;
    }

    /**
     * Retrieves the content of the server message.
     *
     * @return The content of the server message.
     */
    public String getContent() {
        return content;
    }

    /**
     * Retrieves the status of the server message as an enum value.
     * The status represents the type of server message (e.g., login success, error).
     *
     * @return The status of the server message.
     */
    public ServerStatus getStatus() {
        return ServerStatus.fromValue(status);
    }

    /**
     * Enumeration of the possible server message statuses.
     * Each status has a corresponding integer value for easier representation and comparison.
     */
    public enum ServerStatus {
        LOGIN_OK(0),  // Indicates successful login
        ERROR(1),  // Represents an error
        INFO(2),  // Used for informational messages
        DISCONNECT(3),  // Represents a disconnect message
        PING(4);  // Indicates a ping message

        private final int value;

        /**
         * Constructor for ServerStatus enumeration.
         *
         * @param value The integer value associated with the status.
         */
        ServerStatus(int value) {
            this.value = value;
        }

        /**
         * Retrieves the integer value associated with the status.
         *
         * @return The integer value of the status.
         */
        public int getValue() {
            return value;
        }

        /**
         * Converts an integer value into a corresponding ServerStatus enum value.
         *
         * @param value The integer value representing a server status.
         * @return The ServerStatus corresponding to the given value, or null if no match is found.
         */
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
