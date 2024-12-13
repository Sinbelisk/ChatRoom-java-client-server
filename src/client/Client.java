package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;

public class Client extends UDPSocket {
    private final InetAddress serverIp;
    private final int serverPort;
    public Client(InetAddress serverIp, int serverPort) throws SocketException {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }
    @Override
    public void receive() throws IOException {

    }

    @Override
    public void send(byte[] data) throws IOException {
        DatagramPacket packet = new DatagramPacket(data, data.length, serverIp, serverPort);
        getSocket().send(packet);
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client(InetAddress.getLocalHost(), 6969);

        String data = "Porros";
        client.send(data.getBytes());
    }
}
