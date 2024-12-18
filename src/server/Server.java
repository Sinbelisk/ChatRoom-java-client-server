package server;

import common.MessageUtil;
import common.UDPSocket;
import common.models.ChatRoom;
import common.models.message.*;
import common.models.User;

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
        ClientMessage clientMessage = MessageUtil.parseClientMessage(packet.getData());

        if (clientMessage == null) {
            log(Level.SEVERE, "Error parsing packet from %s", packet.getAddress());
            return;
        }

        User user = new User(clientMessage.getNick(), packet.getAddress(), packet.getPort());
        ChatMessage processedMessage = new ChatMessage(clientMessage.getContent(), user, null);

        if (clientMessage.getType() == ClientMessage.COMMAND) {
            handleCommand(processedMessage);
        } else {
            handleChatMessage(processedMessage);
        }
    }

    private void handleCommand(ChatMessage message) {
        User owner = message.getOwner();

        if (!chatRoom.addUser(owner)) {
            log(Level.WARNING, "User %s already in the room", owner.getNick());
            sendMessage(new ServerMessage("Username not available", ServerMessage.ServerStatus.ERROR.getValue()), owner);

        } else {
            log(Level.INFO, "New user entered the room: %s", owner.getNick());

            sendMessage(new ServerMessage("Welcome to the room", ServerMessage.ServerStatus.LOGIN_OK.getValue()));
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
        ServerMessage serverMessage = new ServerMessage(msg.getFormattedContent(), 2);
        byte[] msgData = MessageUtil.createServerMessage(serverMessage);

        chatRoom.getUsers().stream()
                .filter(user -> !user.equals(msg.getOwner()))
                .forEach(user -> send(msgData, user.getIp(), user.getPort()));
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
        ServerMessage historyMessage = new ServerMessage(chatRoom.getMessageHistory(), 2);
        byte[] msgData = MessageUtil.createServerMessage(historyMessage);
        send(msgData, user.getIp(), user.getPort());
    }

}
