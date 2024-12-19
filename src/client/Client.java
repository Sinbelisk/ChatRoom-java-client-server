package client;

import common.util.MessageUtil;
import common.UDPSocket;
import common.models.message.ClientMessage;
import common.models.message.ServerMessage;
import server.ServerConstants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.logging.Level;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Stack;
import java.util.logging.Level;

public class Client extends UDPSocket {
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private boolean connected = false;
    private String nick;

    // Queue to store received messages
    private final Queue<String> messageStack = new LinkedList<>();

    public Client() throws IOException {
        super(DEFAULT_BUFFER_SIZE);
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.OFF);
    }

    @Override
    public void processPacket(DatagramPacket packet) {
        ServerMessage message = MessageUtil.parseServerMessage(packet.getData(), packet.getLength());

        if (message == null) {
            logger.log(Level.SEVERE, "Bad message received");
            return;
        }

        ServerMessage.ServerStatus status = message.getStatus();

        switch (status) {
            case LOGIN_OK -> connected = true;
            case INFO, ERROR -> messageStack.add(message.getContent());
            case DISCONNECT -> handleDisconnectMessage(message);
            case PING -> handlePing();
        }
    }

    private void handlePing() {
        ClientMessage pongMessage = new ClientMessage("pong", nick, ClientMessage.PONG);
        sendMessage(pongMessage);
    }

    private void handleDisconnectMessage(ServerMessage message) {
        messageStack.add(message.getContent());
        disconnect();
    }

    public void connect(String nick) {
        ClientMessage request = new ClientMessage("login", nick, ClientMessage.COMMAND);
        sendMessage(request);
        this.nick = nick;

        receive();  // Wait for confirmation
    }

    public void disconnect() {
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

    // Method to retrieve and clear the message stack
    public String retrieveNextMessage() {
        if (!messageStack.isEmpty()) {
            return messageStack.poll();
        }
        return null;
    }

    // Method to check if there are any pending messages
    public boolean hasMessages() {
        return !messageStack.isEmpty();
    }

    // Gets the current nick associated with this client
    public String getNick() {
        return nick;
    }
}
