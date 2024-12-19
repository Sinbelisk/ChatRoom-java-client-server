package client;

import common.util.MessageUtil;
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
    private String nick;

    public Client() throws IOException {
        super(DEFAULT_BUFFER_SIZE);
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.OFF);
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

        Thread thread = new Thread(() -> {
            while (client.isConnected()) {
                client.receive();
            }
        });

        thread.start();
        while (client.isConnected()) {
            System.out.print("\r> ");
            String msg = s.nextLine().trim();

            if (msg.isEmpty()) {
                System.out.println("Message cannot be empty.");
                continue;
            }

            int type = 0;
            if (msg.startsWith("/")) {
                type = 1;
                msg = msg.substring(1);
            }

            ClientMessage message = new ClientMessage(msg, user, type);
            client.sendMessage(message);
        }

    }

    @Override
    public void processPacket(DatagramPacket packet) {
        ServerMessage message = MessageUtil.parseServerMessage(packet.getData(), packet.getLength());

        if(message == null){
            logger.log(Level.SEVERE, "Bad message received");
            return;
        }

        ServerMessage.ServerStatus status = message.getStatus();

        switch (status) {
            case LOGIN_OK -> {
                connected = true;
            }
            case INFO -> {
                if (message.getContent().equals("ping")) {
                    ClientMessage pongMessage = new ClientMessage("pong", nick, ClientMessage.PONG);
                    sendMessage(pongMessage);
                } else {
                    System.out.println("\r" + message.getContent());
                    System.out.print("\r> ");
                }
            }
            case DISCONNECT -> {
                System.out.println(message.getContent());
                disconnect();
            }
        }
    }

    public void connect(String nick) {
        ClientMessage request = new ClientMessage("login", nick, ClientMessage.COMMAND);
        sendMessage(request);
        this.nick = nick;

        receive();  // Wait for confirmation
    }

    private void disconnect() {
        connected = false;
        nick = "";
        socket.close();
    }

    public void sendMessage(ClientMessage message) {
        send(MessageUtil.createClientMessage(message), ServerConstants.SERVER_ADDRESS, ServerConstants.SERVER_PORT);
    }

    public boolean isConnected() {
        return connected;
    }
}