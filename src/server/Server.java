package server;

import common.UDPSocket;
import common.models.ChatRoom;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Server extends UDPSocket {
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private final ChatRoom chatRoom = new ChatRoom();

    public Server(int port) throws SocketException {
        super(port, DEFAULT_BUFFER_SIZE);

    }

    @Override
    public void processPacket(DatagramPacket packet) {

    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(6969);
        server.receive();
    }
}
