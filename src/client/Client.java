package client;

import common.UDPSocket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;

public class Client extends UDPSocket {
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    public Client() throws SocketException {
        super(DEFAULT_BUFFER_SIZE);
    }

    @Override
    public void processPacket(DatagramPacket packet) {

    }

    public static void main(String[] args) throws Exception {

    }
}
