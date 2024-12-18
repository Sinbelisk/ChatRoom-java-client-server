package server;

import common.MessageUtil;
import common.UDPSocket;
import common.models.ChatRoom;
import common.models.User;
import common.models.message.ChatMessage;
import common.models.message.ClientMessage;
import common.models.message.ServerMessage;

import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

public class Server extends UDPSocket {
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private static final long CLIENT_TIMEOUT = 5000;  // Timeout for waiting for a response to the ping (5 seconds)
    private static final long PING_INTERVAL = 3000;  // Interval to send the ping (3 seconds)
    private final ChatRoom chatRoom = new ChatRoom();
    private final CommandHandler commandHandler;
    private final MessageSender messageSender;
    private final Map<String, Long> clientLastPing = new HashMap<>();  // Map to store the last ping time for each client

    public Server(int port) throws SocketException {
        super(port, DEFAULT_BUFFER_SIZE);
        this.messageSender = new MessageSender(this);
        this.commandHandler = new CommandHandler(chatRoom, this, messageSender);
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(6969);
        server.run();  // Start the server to run the loop
    }

    @Override
    public void processPacket(DatagramPacket packet) {
        ClientMessage clientMessage = MessageUtil.parseClientMessage(packet.getData(), packet.getLength());

        if (clientMessage == null) {
            log(Level.SEVERE, "Error processing a null packet");
            return;
        }

        User user = new User(clientMessage.getNick(), packet.getAddress(), packet.getPort());

        // If the client responds with "pong", update the last ping time
        if (clientMessage.getContent().equals("pong")) {
            log(Level.INFO, "Pong received from client %s", user.getKey());
            clientLastPing.put(user.getKey(), System.currentTimeMillis());
            return;
        }

        if (clientMessage.getType() == ClientMessage.COMMAND) {
            log(Level.INFO, "Command request from user %s: %s", user.getKey(), clientMessage.getContent());
            commandHandler.handleCommand(clientMessage, user);
        } else {
            ChatMessage processedMessage = new ChatMessage(clientMessage.getContent(), user);
            log(Level.INFO, "Message received from client %s: %s", user.getKey(), processedMessage.toString());
            handleChatMessage(processedMessage);
        }
    }

    private void handleChatMessage(ChatMessage msg) {
        if (!chatRoom.hasUser(msg.getOwner())) {
            log(Level.WARNING, "Received a message from an unregistered user: %s", msg.getOwner().getKey());
            return;
        }
        chatRoom.saveMessage(msg);

        log(Level.INFO, "Broadcasting message from %s: %s", msg.getOwner().getKey(), msg.getFormattedContent());
        messageSender.sendBroadcast(msg.getFormattedContent(), chatRoom, msg.getOwner());
    }

    // Send a ping to all clients
    private void sendPing() {
        log(Level.INFO, "Sending ping to %d users", chatRoom.getCapacity());
        chatRoom.getUsers().forEach(user -> {
            ServerMessage pingMessage = new ServerMessage("ping", ServerMessage.ServerStatus.INFO.getValue());
            messageSender.sendToUser(pingMessage, user);  // Send a "ping" message to the user
            log(Level.FINE, "Ping sent to user %s", user.getKey());
        });
        log(Level.INFO, "Ping sending finished.");
    }

    // Check for disconnected clients by inactivity
    private void checkDisconnectedClients() {
        long currentTime = System.currentTimeMillis();
        Iterator<User> iterator = chatRoom.getUsers().iterator();

        while (iterator.hasNext()) {
            User user = iterator.next();
            long lastPingTime = clientLastPing.getOrDefault(user.getKey(), 0L);

            // If the client didn't respond to the ping within the timeout period, remove them
            if (currentTime - lastPingTime > CLIENT_TIMEOUT) {
                log(Level.WARNING, "Client %s disconnected due to inactivity.", user.getKey());
                iterator.remove();  // Remove from the chat room
                messageSender.sendExitMessageToUser("You have been disconnected due to inactivity", user);  // Send a disconnect message
            }
        }
    }

    // Main server loop: send ping and check disconnected clients
    public void run() {
        log(Level.INFO, "Server is running and ready to receive packets.");
        while (true) {
            receive();  // Receive data from clients
            sendPing();  // Send ping messages to all users
            checkDisconnectedClients();  // Check for clients that should be disconnected
        }
    }
}


