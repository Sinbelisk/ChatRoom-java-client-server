package server;

import common.UDPSocket;
import common.models.ChatRoom;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Server extends UDPSocket {
    private final ChatRoom chatRoom = new ChatRoom();
    public Server(int port) throws UnknownHostException {
        super(InetAddress.getLocalHost(), port);
    }
    @Override
    public void receive() {

    }

    @Override
    public void send(byte[] data) {

    }
}
