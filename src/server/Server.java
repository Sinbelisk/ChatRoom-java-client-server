package server;

import common.UDPSocket;
import common.models.ChatRoom;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Server extends UDPSocket {
    private byte[] buffer = new byte[1024];
    private final ChatRoom chatRoom = new ChatRoom();
    public Server(int port) throws UnknownHostException, SocketException {
        super(InetAddress.getLocalHost(), port);
        socket.open();
    }
    @Override
    public void receive() throws IOException {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        getSocket().receive(packet);

        String message = new String(packet.getData());
        System.out.println(message);
    }

    @Override
    public void send(byte[] data) {

    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(6969);
        server.receive();
    }
}
