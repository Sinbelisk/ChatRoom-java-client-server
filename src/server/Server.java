package server;

import common.PacketInterpreter;
import common.UDPSocket;
import common.models.ChatRoom;
import common.models.Message;
import common.models.MessageType;
import common.models.User;

import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.Set;
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

        Message msg = interpreter.parsePacket(packet);
        if(msg == null){
            log(Level.SEVERE, "SEVERE ERROR WHEN PARSING PACKET WITH ORIGIN %s", packet.getAddress());
            return;
        }

        if (msg.getType() == MessageType.COMMAND) {
            User owner = msg.getOwner();
            chatRoom.addUser(owner);
            log(Level.INFO, "New user entered the room: %s", owner.getNick());
        }
        else {
            broadcastMessage(msg);
        }
    }

    private void broadcastMessage(Message message) {
        User owner = message.getOwner();
        Set<User> users = chatRoom.getUsers();
        byte[] msgData = message.getBytes();

        for (User user : users) {
            if(!user.equals(owner)){
                send(msgData, user.getIp(), user.getPort());
            }
        }
    }
}