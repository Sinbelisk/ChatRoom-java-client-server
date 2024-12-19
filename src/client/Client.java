package client;

import common.util.MessageUtil;
import common.UDPSocket;
import client.model.message.ClientMessage;
import server.model.message.ServerMessage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;

/**
 * Represents a client in the UDP-based communication system. The client connects to a server, sends and receives messages,
 * and handles specific server responses such as login, disconnect, and ping.
 *
 * The client maintains the connection state and manages a queue of received messages.
 */
public class Client extends UDPSocket {

    private static final int PACKET_RECEPTION_TIMEOUT = 5000; // 5 seconds
    private static final int DEFAULT_BUFFER_SIZE = 1024; // Default buffer size for receiving packets
    private boolean connected = false;
    private String nick;
    private final Queue<String> messageQueue = new LinkedList<>();  // Queue to store received messages
    private final InetAddress serverAddress;
    private final int serverPort;

    /**
     * Creates a new client and initializes the UDP socket for communication.
     *
     * @param serverAddress The IP address of the server to connect to.
     * @param serverPort The port number of the server.
     * @throws IOException If an error occurs while setting up the socket.
     */
    public Client(InetAddress serverAddress, int serverPort) throws IOException {
        super(DEFAULT_BUFFER_SIZE);
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.OFF);
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;

        socket.setSoTimeout(PACKET_RECEPTION_TIMEOUT); // Set socket timeout for receiving packets
    }

    /**
     * Processes the received packet from the server. The packet is parsed and its content is handled
     * based on the server message status.
     *
     * @param packet The received DatagramPacket.
     */
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
            case INFO, ERROR -> messageQueue.add(message.getContent());
            case DISCONNECT -> handleDisconnectMessage(message);
            case PING -> handlePing();
        }
    }

    /**
     * Handles the ping message received from the server by sending a pong message back.
     */
    private void handlePing() {
        ClientMessage pongMessage = new ClientMessage("pong", nick, ClientMessage.PONG);
        sendMessage(pongMessage);
    }

    /**
     * Handles a disconnect message received from the server. It adds the content of the message
     * to the message stack and disconnects the client.
     *
     * @param message The disconnect message from the server.
     */
    private void handleDisconnectMessage(ServerMessage message) {
        messageQueue.add(message.getContent());
        disconnect();
    }

    /**
     * Initiates a login request to the server with the provided nickname.
     *
     * @param nick The nickname to use for logging in.
     */
    public void connect(String nick) {
        ClientMessage request = new ClientMessage("login", nick, ClientMessage.COMMAND);
        sendMessage(request);
        this.nick = nick;

        receive();  // Wait for confirmation from the server
    }

    /**
     * Disconnects the client from the server and closes the socket.
     */
    public void disconnect() {
        connected = false;
        nick = "";
        socket.close();
    }

    /**
     * Sends a message to the server.
     *
     * @param message The message to be sent.
     */
    public void sendMessage(ClientMessage message) {
        send(MessageUtil.createClientMessage(message), serverAddress, serverPort);
    }

    /**
     * Checks if the client is currently connected to the server.
     *
     * @return True if the client is connected, false otherwise.
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Retrieves and removes the next message from the message stack. If the stack is empty, returns null.
     *
     * @return The next message from the stack, or null if the stack is empty.
     */
    public String retrieveNextMessage() {
        if (!messageQueue.isEmpty()) {
            return messageQueue.poll();
        }
        return null;
    }

    /**
     * Checks if there are any messages in the message stack.
     *
     * @return True if there are messages in the stack, false otherwise.
     */
    public boolean hasMessages() {
        return !messageQueue.isEmpty();
    }

    /**
     * Retrieves the current nickname of the client.
     *
     * @return The nickname of the client.
     */
    public String getNick() {
        return nick;
    }
}