package client;

import common.PacketInterpreter;
import common.UDPSocket;
import common.models.User;
import common.models.message.ClientMessage;
import common.models.message.MessageType;
import common.models.message.ServerMessage;
import common.utils.MessageSerializer;
import common.utils.ServerMessageSerializer;
import server.ServerConstants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;

public class Client extends UDPSocket {
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private boolean connected = false;
    public Client() throws IOException {
        super(DEFAULT_BUFFER_SIZE);
        FileHandler fileHandler = new FileHandler("chat.log", true);
        logger.addHandler(fileHandler);
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.OFF);
    }

    @Override
    public void processPacket(DatagramPacket packet) {
        ServerMessage message;

        try{
            message = ServerMessageSerializer.deserialize(packet.getData());
            System.out.println("\r" + message.getContent());
            System.out.print("\r> ");
        } catch (IOException e){

        }
    }

    public boolean connect(String nick){
        ClientMessage connectionRequest = new ClientMessage("", user, MessageType.CONNECTION_REQUEST);

        try{
            byte[] request = MessageSerializer.serialize(connectionRequest);
            send(request, ServerConstants.SERVER_ADDRESS, ServerConstants.SERVER_PORT);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean isConnected() {
        return connected;
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client();
        Scanner s = new Scanner(System.in);

        do {
            System.out.print("Nombre de Usuario: ");
            String user = s.nextLine();

            client.connect(user);
        } while (!client.isConnected());

        Thread thread = new Thread(()->{
            while (true){
                client.receive();
            }
        });

        thread.start();
        while(true){
            System.out.print("\r> ");
            String msg = s.nextLine();

            String test = String.format("msg;%s;%s", user, msg);
            client.send(test.getBytes(), InetAddress.getLocalHost(), 6969);
        }
    }
}
