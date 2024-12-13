package client;

import common.PacketInterpreter;
import common.UDPSocket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Map;

public class Client extends UDPSocket {
    private final PacketInterpreter interpreter = new PacketInterpreter();
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    public Client() throws SocketException {
        super(DEFAULT_BUFFER_SIZE);
    }

    @Override
    public void processPacket(DatagramPacket packet) {
        String msg = new String(packet.getData());
        System.out.println(msg);
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client();

        String bolivia = "bolivia";
        client.send(bolivia.getBytes(), InetAddress.getLocalHost(), 6969);

        client.receive();

    }
}
