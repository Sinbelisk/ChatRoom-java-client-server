package client.model.message;

/**
 * Represents a message sent by the client to the server in the communication system.
 * The message contains the content, the sender's nickname, and the type of message (either a regular message or a command).
 */
public class ClientMessage {

    /**
     * Message type constant indicating a command message.
     */
    public static final int COMMAND = 1;

    /**
     * Message type constant indicating a regular message.
     */
    public static final int MSG = 0;

    /**
     * Message type constant indicating a pong message.
     */
    public static final int PONG = 2;

    private final String content;  // The content of the message
    private final String nick;     // The nickname of the sender
    private final int type;        // The type of the message (0 for regular message, 1 for command)

    /**
     * Creates a new client message with the specified content, sender nickname, and message type.
     *
     * @param content The content of the message.
     * @param nick The nickname of the sender.
     * @param type The type of the message (0 for regular message, 1 for command, 2 for pong).
     */
    public ClientMessage(String content, String nick, int type) {
        this.content = content;
        this.nick = nick;
        this.type = type;
    }

    /**
     * Gets the content of the message.
     *
     * @return The content of the message.
     */
    public String getContent() {
        return content;
    }

    /**
     * Gets the nickname of the sender.
     *
     * @return The nickname of the sender.
     */
    public String getNick() {
        return nick;
    }

    /**
     * Gets the type of the message (0 for regular message, 1 for command, 2 for pong).
     *
     * @return The type of the message.
     */
    public int getType() {
        return type;
    }
}

