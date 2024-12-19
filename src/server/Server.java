package server;

import common.util.MessageUtil;
import common.UDPSocket;
import server.model.ChatRoom;
import server.model.User;
import server.model.message.ChatMessage;
import client.model.message.ClientMessage;
import server.commands.CommandHandler;

import java.net.DatagramPacket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;


/**
 * The Server class extends UDPSocket and acts as the central server for handling client messages
 * in a chat room. It processes different types of client messages, handles user connections and disconnections,
 * and manages communication within the chat room.
 * It is responsible for receiving and processing client messages, sending pings to inactive users,
 * and broadcasting messages to all active users in the chat room.
 */
public class Server extends UDPSocket {
    private static final int DEFAULT_BUFFER_SIZE = 1024;  // Default buffer size for UDP packets
    private static final int MAX_PING_COUNT = 3;  // Maximum number of pings before considering a user inactive
    private final ChatRoom chatRoom = new ChatRoom();  // The chat room where all users and messages are stored
    private final CommandHandler commandHandler;  // Command handler for processing client commands
    private final MessageSender messageSender;  // Message sender for sending messages to clients
    private final Map<String, Integer> pingCounters = new HashMap<>();  // Map to track ping counts for users

    /**
     * Constructor to initialize the server with a specific port.
     * Initializes the message sender and command handler.
     *
     * @param port The port on which the server will listen for incoming packets.
     * @throws SocketException If there is an error with the UDP socket.
     */
    public Server(int port) throws SocketException, UnknownHostException {
        super(port, DEFAULT_BUFFER_SIZE);
        this.messageSender = new MessageSender(this);  // Create a new message sender
        this.commandHandler = new CommandHandler(chatRoom, messageSender);  // Create a new command handler
    }

    /**
     * Processes an incoming UDP packet from a client.
     * Depending on the type of message, it will either handle a PONG, command, or regular chat message.
     *
     * @param packet The UDP packet received from a client.
     */
    @Override
    public void processPacket(DatagramPacket packet) {
        ClientMessage clientMessage = MessageUtil.parseClientMessage(packet.getData(), packet.getLength());

        if (clientMessage == null) {
            log(Level.SEVERE, "Error, a null or wrong packet arrived");
            return;
        }

        User user = new User(clientMessage.getNick(), packet.getAddress(), packet.getPort());
        chatRoom.updateActivity(user);  // Update the last activity timestamp for the user
        String userKey = user.getKey();

        switch (clientMessage.getType()) {
            case ClientMessage.PONG -> handlePong(userKey);  // Handle pong responses
            case ClientMessage.COMMAND -> handleCommand(clientMessage, user);  // Handle client commands
            case ClientMessage.MSG -> handleMessage(clientMessage, user);  // Handle regular chat messages
            default -> log(Level.WARNING, "Unknown message type '%d' from user %s", clientMessage.getType(), userKey);
        }

        pingCounters.put(userKey, 0);  // Reset ping counter on message receipt
    }

    /**
     * Handles a PONG message from a user.
     *
     * @param userKey The key of the user that sent the PONG message.
     */
    private void handlePong(String userKey) {
        log(Level.INFO, "Pong received from user %s", userKey);
    }

    /**
     * Handles a command request from a client.
     * It delegates command handling to the CommandHandler.
     *
     * @param clientMessage The client message containing the command.
     * @param user The user who sent the command.
     */
    private void handleCommand(ClientMessage clientMessage, User user) {
        log(Level.INFO, "Command request from user %s: %s", user.getKey(), clientMessage.getContent());
        commandHandler.handleCommand(clientMessage, user);  // Delegate to command handler
    }

    /**
     * Handles a regular chat message from a client.
     * It saves the message in the chat room and broadcasts it to all users.
     *
     * @param clientMessage The chat message received from the client.
     * @param user The user who sent the message.
     */
    private void handleMessage(ClientMessage clientMessage, User user) {
        if (!chatRoom.hasUser(user)) {
            log(Level.WARNING, "Received message from unregistered user %s", user.getKey());
            return;
        }
        ChatMessage msg = new ChatMessage(clientMessage.getContent(), user);  // Create a new chat message
        chatRoom.saveMessage(msg);  // Save the message to the chat room
        log(Level.INFO, "Broadcasting message from %s: %s", user.getKey(), msg.getFormattedContent());
        messageSender.sendBroadcast(msg.getFormattedContent(), chatRoom, user);  // Broadcast the message to all users
    }

    /**
     * Starts the server and continuously listens for incoming packets from clients.
     * It checks for inactive users and sends pings to detect unresponsive users.
     */
    public void run() {
        log(Level.INFO, "Server is running and ready to receive packets.");
        while (true) {
            receive();  // Wait to receive a packet

            // If there are inactive users, send pings and check for timeouts
            if(chatRoom.hasInactiveUsers()) {
                sendPingsAndCheckTimeouts();
            }
        }
    }

    /**
     * Sends pings to inactive users and handles timeouts for users who do not respond to pings.
     * If a user does not respond to the maximum allowed pings, they are removed from the chat room.
     */
    private void sendPingsAndCheckTimeouts() {
        Set<User> inactiveUsers = chatRoom.getInactiveUsers();  // Get the list of inactive users
        log(Level.INFO, "Sending pings to inactive users...");
        for (User user : inactiveUsers) {
            String userKey = user.getKey();
            pingCounters.put(userKey, pingCounters.getOrDefault(userKey, 0) + 1);  // Increment ping counter

            // If a user did not respond to the maximum allowed pings, remove them
            if (pingCounters.get(userKey) >= MAX_PING_COUNT) {
                handleInactiveUser(user);
            } else {
                messageSender.sendPing(user);  // Send a ping to the inactive user
            }
        }
        log(Level.INFO, "Ping sending completed!");
    }

    /**
     * Handles the removal of a user who has not responded to the maximum number of pings.
     * The user is removed from the chat room, and a broadcast message is sent to other users.
     *
     * @param user The inactive user to be removed.
     */
    private void handleInactiveUser(User user) {
        log(Level.WARNING, "User %s did not respond to pings and will be removed.", user.getKey());
        chatRoom.removeUser(user);  // Remove the user from the chat room
        pingCounters.remove(user.getKey());  // Remove the user's ping count
        messageSender.sendBroadcast(String.format("User %s has been disconnected due to inactivity.", user.getNick()), chatRoom, null);  // Broadcast disconnection message
    }
}
