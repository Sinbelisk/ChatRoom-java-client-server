package server;

import common.MessageUtil;
import common.UDPSocket;
import common.models.ChatRoom;
import common.models.message.*;
import common.models.User;

import java.net.*;
import java.util.logging.Level;

import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.logging.Level;

public class Server extends UDPSocket {
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private final ChatRoom chatRoom = new ChatRoom();

    public Server(int port) throws SocketException {
        super(port, DEFAULT_BUFFER_SIZE);
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(6969);
        while (true) {
            server.receive();
        }
    }

    @Override
    public void processPacket(DatagramPacket packet) {
        log(Level.INFO, "Received packet from %s:%d", packet.getAddress(), packet.getPort());
        ClientMessage clientMessage = MessageUtil.parseClientMessage(packet.getData(), packet.getLength());

        if (clientMessage == null) {
            log(Level.SEVERE, "Error parsing packet from %s", packet.getAddress());
            return;
        }

        User user = new User(clientMessage.getNick(), packet.getAddress(), packet.getPort());
        ChatMessage processedMessage = new ChatMessage(clientMessage.getContent(), user, null);
        log(Level.INFO, "Processed message from user %s: %s", user.getNick(), clientMessage.getContent());

        if (clientMessage.getType() == ClientMessage.COMMAND) {
            handleCommand(processedMessage);
        } else {
            handleChatMessage(processedMessage);
        }
    }

    private void handleCommand(ChatMessage message) {
        User owner = message.getOwner();
        String[] elements = message.getContent().split("\\s+");

        switch (elements[0]) {
            case "login": handleLogin(owner); break;
            case "list": sendMessage(new ServerMessage(chatRoom.listUsers(), ServerMessage.ServerStatus.INFO.getValue()), owner); break;
            case "private": handlePrivateMessage(elements, message, owner); break;
        }
    }

    private void handlePrivateMessage(String[] elements, ChatMessage message, User owner) {
        User whisperUser = chatRoom.getUserByNick(elements[1]);
        if (whisperUser == null) {
            sendMessage(new ServerMessage("That user does not exist", ServerMessage.ServerStatus.ERROR.getValue()), owner);
            return;
        }
        String privateMsg = message.getContent().substring(elements[1].length()).trim();
        broadcastMessage(new ChatMessage(privateMsg, owner, MessageType.MSG), whisperUser);
    }

    private void handleLogin(User owner) {
        if (!chatRoom.addUser(owner)) {
            log(Level.WARNING, "User %s already in the room", owner.getNick());
            sendMessage(new ServerMessage("Username not available", ServerMessage.ServerStatus.ERROR.getValue()), owner);
        } else {
            log(Level.INFO, "New user entered the room: %s", owner.getNick());
            sendMessage(new ServerMessage("Welcome to the room", ServerMessage.ServerStatus.LOGIN_OK.getValue()), owner);
            broadcastHistory(owner);
        }
    }

    private void handleChatMessage(ChatMessage msg) {
        if (!chatRoom.hasUser(msg.getOwner())) {
            log(Level.WARNING, "User %s not in the room", msg.getOwner().getNick());
            return;
        }
        chatRoom.saveMessage(msg);
        broadcastMessage(msg);
    }

    private void broadcastMessage(ChatMessage msg) {
        ServerMessage serverMessage = new ServerMessage(msg.getFormattedContent(), ServerMessage.ServerStatus.INFO.getValue());
        byte[] msgData = MessageUtil.createServerMessage(serverMessage);
        chatRoom.getUsers().stream()
                .filter(user -> !user.equals(msg.getOwner()))
                .forEach(user -> send(msgData, user.getIp(), user.getPort()));
    }

    private void broadcastMessage(ChatMessage msg, User user){
        ServerMessage serverMessage = new ServerMessage(msg.getFormattedContent(), ServerMessage.ServerStatus.INFO.getValue());
        byte[] msgData = MessageUtil.createServerMessage(serverMessage);
        send(msgData, user.getIp(), user.getPort());
    }

    private void sendMessage(ServerMessage message, User user) {
        byte[] msgData = MessageUtil.createServerMessage(message);
        send(msgData, user.getIp(), user.getPort());
    }

    private void sendMessage(ServerMessage message) {
        byte[] msgData = MessageUtil.createServerMessage(message);
        chatRoom.getUsers().forEach(user -> send(msgData, user.getIp(), user.getPort()));
    }

    private void broadcastHistory(User user) {
        String messageHistory = chatRoom.getMessageHistory().isEmpty() ? "No messages found" : chatRoom.getMessageHistory();
        ServerMessage historyMessage = new ServerMessage(messageHistory, ServerMessage.ServerStatus.INFO.getValue());
        byte[] msgData = MessageUtil.createServerMessage(historyMessage);
        send(msgData, user.getIp(), user.getPort());
    }
}