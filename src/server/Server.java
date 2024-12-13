package server;

import common.PacketInterpreter;
import common.UDPSocket;
import common.models.ChatRoom;
import common.response.Response;
import common.response.ResponseFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map;

public class Server extends UDPSocket {
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private final ChatRoom chatRoom = new ChatRoom();
    private final PacketInterpreter interpreter = new PacketInterpreter();

    public Server(int port) throws SocketException {
        super(port, DEFAULT_BUFFER_SIZE);

    }

    @Override
    public void processPacket(DatagramPacket packet) {
        InetAddress sourceAddress = packet.getAddress();
        int sourcePort = packet.getPort();

        Map<String, String> parsedData = interpreter.parsePacket(packet);
        Response response = ResponseFactory.createResponse(parsedData);

        send(response.getBytes(), sourceAddress, sourcePort);
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(6969);

        while(true){
            server.receive();
        }
    }
}
