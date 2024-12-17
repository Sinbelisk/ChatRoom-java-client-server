package server;

import common.PacketInterpreter;
import common.UDPSocket;
import common.models.ChatRoom;
import common.models.message.ClientMessage;
import common.models.message.MessageType;
import common.models.User;
import common.models.message.ServerMessage;
import common.models.message.ServerStatus;
import common.utils.ServerMessageSerializer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.logging.Level;

public class Server extends UDPSocket {
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private final ChatRoom chatRoom = new ChatRoom();
    private final PacketInterpreter interpreter = new PacketInterpreter();

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
        ClientMessage msg = interpreter.parsePacket(packet);
        if (msg == null) {
            log(Level.SEVERE, "Error parsing packet from %s", packet.getAddress());
            return;
        }

        if (msg.getType() == MessageType.COMMAND) {
            handleCommand(msg);
        } else {
            handleChatMessage(msg);
        }
    }

    private void handleCommand(ClientMessage msg) {
        User owner = msg.getOwner();

        if (!chatRoom.addUser(owner)) {
            log(Level.WARNING, "User %s already in the room", owner.getNick());
            sendMessage(new ServerMessage("Username not available", ServerStatus.ERROR), owner);
        } else {
            log(Level.INFO, "New user entered the room: %s", owner.getNick());
            broadcastHistory(owner);
        }
    }

    private void handleChatMessage(ClientMessage msg) {
        if (!chatRoom.hasUser(msg.getOwner())) {
            log(Level.WARNING, "User %s not in the room", msg.getOwner().getNick());
            return;
        }

        chatRoom.saveMessage(msg);
        broadcastMessage(msg);
    }

    private void broadcastMessage(ClientMessage msg) {
        byte[] msgData = msg.getBytes();

        chatRoom.getUsers().stream()
                .filter(user -> !user.equals(msg.getOwner()))
                .forEach(user -> send(msgData, user.getIp(), user.getPort()));
    }

    private void sendMessage(ServerMessage content, User user) {
        send(content.getContent().getBytes(), user.getIp(), user.getPort());
    }

    private void sendMessage(ServerMessage message) {
        byte[] msgData;

        try{
            msgData = ServerMessageSerializer.serialize(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        chatRoom.getUsers().forEach(user -> send(msgData, user.getIp(), user.getPort()));
    }

    private void broadcastHistory(User user) {
        send(chatRoom.getMessageHistory().getBytes(), user.getIp(), user.getPort());
    }
}
