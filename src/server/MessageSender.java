package server;

import common.util.MessageUtil;
import common.util.SimpleLogger;
import common.UDPOperation;
import server.model.ChatRoom;
import server.model.User;
import server.model.message.ServerMessage;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The MessageSender class is responsible for sending messages to users via UDP.
 * It interfaces with a `UDPOperation` to send messages to either a single user, multiple users,
 * or broadcast messages to a chat room. It also provides various message types such as information,
 * error, login, and exit messages.
 */
public class MessageSender {
    private final UDPOperation udpOperation;  // Reference to the UDPSocket operation
    private static final Logger logger = SimpleLogger.getInstance().getLogger(MessageSender.class);  // Logger for the class

    /**
     * Constructs a MessageSender instance with a reference to the common UDP interface.
     *
     * @param udpOperation The UDPOperation instance used to send UDP packets.
     */
    public MessageSender(UDPOperation udpOperation) {
        this.udpOperation = udpOperation;  // Store the reference to the UDPSocket
        logger.log(Level.INFO, "MessageSender initialized");
    }

    /**
     * Helper method to send a message to a specific user.
     *
     * @param message The message to be sent.
     * @param user The user to whom the message will be sent.
     */
    private void sendMessage(ServerMessage message, User user) {
        byte[] msgData = MessageUtil.createServerMessage(message);  // Convert the message to a byte array
        udpOperation.send(msgData, user.getIp(), user.getPort());  // Send the message to the user
    }

    /**
     * Sends a message to a single user.
     *
     * @param message The message content.
     * @param status The status code indicating the message type.
     * @param user The recipient user.
     */
    public void sendToUser(String message, int status, User user) {
        sendMessage(new ServerMessage(message, status), user);  // Create ServerMessage and send it
    }

    /**
     * Sends a broadcast message to all users in the chat room, excluding the sender.
     *
     * @param message The message content.
     * @param status The status code indicating the message type.
     * @param chatRoom The chat room containing all users.
     * @param owner The user sending the message (to be excluded from the broadcast).
     */
    public void sendBroadcast(String message, int status, ChatRoom chatRoom, User owner) {
        ServerMessage serverMessage = new ServerMessage(message, status);
        chatRoom.getUsers().stream()
                .filter(user -> !user.equals(owner))  // Exclude the owner from the broadcast
                .forEach(user -> sendMessage(serverMessage, user));  // Send the message to each user
    }

    /**
     * Simplified method for sending a broadcast message to all users,
     * defaulting to an informational message status.
     *
     * @param message The message content.
     * @param chatRoom The chat room containing all users.
     * @param owner The user sending the message.
     */
    public void sendBroadcast(String message, ChatRoom chatRoom, User owner) {
        sendBroadcast(message, ServerMessage.ServerStatus.INFO.getValue(), chatRoom, owner);
    }

    /**
     * Sends the message history to a specific user.
     *
     * @param messageHistory The formatted message history.
     * @param user The user to whom the history will be sent.
     */
    public void sendHistoryToUser(String messageHistory, User user) {
        sendToUser(messageHistory, ServerMessage.ServerStatus.INFO.getValue(), user);
    }

    /**
     * Sends an informational message to a user.
     *
     * @param msg The informational message.
     * @param user The recipient user.
     */
    public void sendInfoToUser(String msg, User user) {
        sendToUser(msg, ServerMessage.ServerStatus.INFO.getValue(), user);
    }

    /**
     * Sends a login success message to a user.
     *
     * @param msg The login success message.
     * @param user The user to whom the message will be sent.
     */
    public void sendLoginMessageToUser(String msg, User user) {
        sendToUser(msg, ServerMessage.ServerStatus.LOGIN_OK.getValue(), user);
    }

    /**
     * Sends an exit message to a user, indicating that their connection has been terminated.
     *
     * @param msg The exit message.
     * @param user The user to whom the exit message will be sent.
     */
    public void sendExitMessageToUser(String msg, User user) {
        sendToUser(msg, ServerMessage.ServerStatus.DISCONNECT.getValue(), user);
    }

    /**
     * Sends an error message to a user.
     *
     * @param msg The error message.
     * @param user The user to whom the error message will be sent.
     */
    public void sendErrorToUser(String msg, User user) {
        sendToUser(msg, ServerMessage.ServerStatus.ERROR.getValue(), user);
    }

    /**
     * Sends a ping message to a user to check if they are still active.
     *
     * @param user The user to whom the ping message will be sent.
     */
    public void sendPing(User user) {
        sendToUser("ping", ServerMessage.ServerStatus.PING.getValue(), user);
    }
}

