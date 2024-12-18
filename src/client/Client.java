package client;

import common.MessageUtil;
import common.UDPSocket;
import common.models.message.ClientMessage;
import common.models.message.ServerMessage;
import server.ServerConstants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Scanner;
import java.util.logging.Level;

public class Client extends UDPSocket {
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private boolean connected = false;
    public Client() throws IOException {
        super(DEFAULT_BUFFER_SIZE);
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.OFF);
    }

    @Override
    public void processPacket(DatagramPacket packet) {
        ServerMessage message = MessageUtil.parseServerMessage(packet.getData(), packet.getLength());
        ServerMessage.ServerStatus status = message.getStatus();

        switch (status){
            case LOGIN_OK -> {
                connected = true;
            }
            case INFO -> {
            }
            case DISCONNECT -> {
                connected = false;
            }
        }

        if(connected){
            System.out.println("\r" + message.getContent());
            System.out.print("\r> ");
        }
    }

    public void connect(String nick){
        ClientMessage request = new ClientMessage("login", nick, ClientMessage.COMMAND);
        sendMessage(request);

        receive();
    }

    public void sendMessage(ClientMessage message){
        send(MessageUtil.createClientMessage(message), ServerConstants.SERVER_ADDRESS, ServerConstants.SERVER_PORT);
    }

    public boolean isConnected() {
        return connected;
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client();
        String user = "";
        Scanner s = new Scanner(System.in);

        do {
            System.out.print("Nombre de Usuario: ");
            user = s.nextLine();

            client.connect(user);
        } while (!client.isConnected());

        Thread thread = new Thread(()->{
            while (true){
                client.receive();
            }
        });

        thread.start();
        while(client.isConnected()){
            System.out.print("\r> ");
            String msg = s.nextLine();

            int type = 0;
            if(msg.startsWith("/")){
                type = 1;
                msg = msg.substring(1);
            }

            ClientMessage message = new ClientMessage(msg, user, type);
            client.sendMessage(message);
        }
    }
}
