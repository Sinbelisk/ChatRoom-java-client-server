package server;

import common.MessageUtil;
import common.SimpleLogger;
import common.UDPSocket;
import common.models.ChatRoom;
import common.models.User;
import common.models.message.ServerMessage;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageSender {
    private final UDPSocket udpSocket;
    private static final Logger logger = SimpleLogger.getInstance().getLogger(MessageSender.class);

    // Constructor that takes a reference to UDPSocket (like Server)
    public MessageSender(UDPSocket udpSocket) {
        this.udpSocket = udpSocket;  // Store the reference to the UDPSocket
        logger.log(Level.INFO, "MessageSender initialized");
    }

    // Send a message to a single user
    public void sendToUser(ServerMessage message, User user) {
        byte[] msgData = MessageUtil.createServerMessage(message);  // Convert the message to a byte array
        udpSocket.send(msgData, user.getIp(), user.getPort());  // Send the message to the specified user
    }

    // Send a broadcast message to all users (except the sender)
    public void sendBroadcast(ServerMessage message, ChatRoom chatRoom, User owner) {
        byte[] msgData = MessageUtil.createServerMessage(message);  // Convert the message to a byte array

        // Send the message to all users in the chat room except the owner (sender)
        chatRoom.getUsers().stream()
                .filter(user -> !user.equals(owner))
                .forEach(user -> udpSocket.send(msgData, user.getIp(), user.getPort()));  // Send the message
    }

    public void sendBroadcast(String message, ChatRoom chatRoom, User owner) {
        ServerMessage serverMessage = new ServerMessage(message, ServerMessage.ServerStatus.INFO.getValue());
        sendBroadcast(serverMessage, chatRoom, owner);
    }

    // Send message history to a user
    public void sendHistoryToUser(String messageHistory, User user) {
        ServerMessage historyMessage = new ServerMessage(messageHistory, ServerMessage.ServerStatus.INFO.getValue());
        sendToUser(historyMessage, user);
    }

    public void sendInfoToUser(String msg, User user) {
        ServerMessage message = new ServerMessage(msg, ServerMessage.ServerStatus.INFO.getValue());
        sendToUser(message, user);
    }

    public void sendLoginMessageToUser(String msg, User user) {
        ServerMessage message = new ServerMessage(msg, ServerMessage.ServerStatus.LOGIN_OK.getValue());
        sendToUser(message, user);
    }

    public void sendExitMessageToUser(String msg, User user) {
        ServerMessage message = new ServerMessage(msg, ServerMessage.ServerStatus.DISCONNECT.getValue());
        sendToUser(message, user);
    }

    public void sendErrorToUser(String msg, User user) {
        ServerMessage message = new ServerMessage(msg, ServerMessage.ServerStatus.ERROR.getValue());
        sendToUser(message, user);
    }
}