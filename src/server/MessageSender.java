package server;

import common.util.MessageUtil;
import common.util.SimpleLogger;
import common.UDPOperation;
import common.models.ChatRoom;
import common.models.User;
import common.models.message.ServerMessage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageSender {
    private final UDPOperation udpOperation;
    private static final Logger logger = SimpleLogger.getInstance().getLogger(MessageSender.class);

    // Constructor that takes a reference to the common UDP interface (like Server)
    public MessageSender(UDPOperation udpOperation) {
        this.udpOperation = udpOperation;  // Store the reference to the UDPSocket
        logger.log(Level.INFO, "MessageSender initialized");
    }

    // Helper method to send a message to a user
    private void sendMessage(ServerMessage message, User user) {
        byte[] msgData = MessageUtil.createServerMessage(message);  // Convert the message to a byte array
        udpOperation.send(msgData, user.getIp(), user.getPort());  // Send the message to the specified user
    }

    // Send a message to a single user
    public void sendToUser(String message, int status, User user) {
        sendMessage(new ServerMessage(message, status), user);
    }

    // Send a broadcast message to all users (except the sender)
    public void sendBroadcast(String message, int status, ChatRoom chatRoom, User owner) {
        ServerMessage serverMessage = new ServerMessage(message, status);
        chatRoom.getUsers().stream()
                .filter(user -> !user.equals(owner))
                .forEach(user -> sendMessage(serverMessage, user));
    }

    // Overloaded sendBroadcast for simplified message sending
    public void sendBroadcast(String message, ChatRoom chatRoom, User owner) {
        sendBroadcast(message, ServerMessage.ServerStatus.INFO.getValue(), chatRoom, owner);
    }

    // Send message history to a user
    public void sendHistoryToUser(String messageHistory, User user) {
        sendToUser(messageHistory, ServerMessage.ServerStatus.INFO.getValue(), user);
    }

    // Send informational messages to a user
    public void sendInfoToUser(String msg, User user) {
        sendToUser(msg, ServerMessage.ServerStatus.INFO.getValue(), user);
    }

    // Send login success message to a user
    public void sendLoginMessageToUser(String msg, User user) {
        sendToUser(msg, ServerMessage.ServerStatus.LOGIN_OK.getValue(), user);
    }

    // Send exit message to a user
    public void sendExitMessageToUser(String msg, User user) {
        sendToUser(msg, ServerMessage.ServerStatus.DISCONNECT.getValue(), user);
    }

    // Send error message to a user
    public void sendErrorToUser(String msg, User user) {
        sendToUser(msg, ServerMessage.ServerStatus.ERROR.getValue(), user);
    }

    // Send ping message to a user
    public void sendPing(User user) {
        sendToUser("ping", ServerMessage.ServerStatus.INFO.getValue(), user);
    }
}
