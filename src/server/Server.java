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
import java.util.Map;
import java.util.logging.Level;

import java.util.Iterator;

public class Server extends UDPSocket {
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private static final long PING_INTERVAL = 300;  // 2 minutos en milisegundos
    private static final int MAX_PING_ATTEMPTS = 5;

    private final ChatRoom chatRoom = new ChatRoom();
    private final CommandHandler commandHandler;
    private final MessageSender messageSender;
    private final Map<String, Long> clientLastPing = new HashMap<>();
    private long lastPingTime;

    public Server(int port) throws SocketException {
        super(port, DEFAULT_BUFFER_SIZE);
        this.messageSender = new MessageSender(this);
        this.commandHandler = new CommandHandler(chatRoom, this, messageSender);
        this.lastPingTime = System.currentTimeMillis();
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(6969);
        server.run();  // Start the server to run the loop
    }

    @Override
    public void processPacket(DatagramPacket packet) {
        ClientMessage clientMessage = MessageUtil.parseClientMessage(packet.getData(), packet.getLength());

        if (clientMessage == null) {
            log(Level.SEVERE, "Error, a null or wrong packet arrived");
            return;
        }

        User user = new User(clientMessage.getNick(), packet.getAddress(), packet.getPort());

        // If the client responds with "pong", update the last ping time
        if (clientMessage.getContent().equals("pong")) {
            log(Level.INFO, "Pong received from client %s", user.getKey());
            clientLastPing.put(user.getKey(), System.currentTimeMillis());
            user.setPingAttempts(0);
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

    private void sendPing() {
        log(Level.INFO, "Sending ping to %d users", chatRoom.getCapacity());
        chatRoom.getUsers().forEach(user -> {
            ServerMessage pingMessage = new ServerMessage("ping", ServerMessage.ServerStatus.INFO.getValue());
            messageSender.sendToUser(pingMessage, user);  // Send a "ping" message to the user
            log(Level.FINE, "Ping sent to user %s", user.getKey());
        });
        log(Level.INFO, "Ping sending finished.");
        lastPingTime = System.currentTimeMillis();
    }

    private void checkInactiveClients() {
        long currentTime = System.currentTimeMillis();

        Iterator<User> iterator = chatRoom.getUsers().iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            long lastPing = clientLastPing.getOrDefault(user.getKey(), 0L);

            if (currentTime - lastPing > PING_INTERVAL) {
                int attempts = user.getPingAttempts() + 1;
                user.setPingAttempts(attempts);

                if (attempts >= MAX_PING_ATTEMPTS) {
                    log(Level.WARNING, "Client %s has been expelled for inactivity.", user.getKey());
                    chatRoom.removeUser(user); // Eliminar del chatRoom
                    iterator.remove(); // Eliminar del iterador
                } else {
                    log(Level.WARNING, "Client %s did not respond, attempt %d/%d", user.getKey(), attempts, MAX_PING_ATTEMPTS);
                }
            }
        }
    }



    // Main server loop: send ping and check disconnected clients
    public void run() {
        log(Level.INFO, "Server is running and ready to receive packets.");
        while (true) {
            receive();  // Receive data from clients
            long currentTime = System.currentTimeMillis();

            // Check if it's time to send a ping
            if (currentTime - lastPingTime >= PING_INTERVAL) {
                sendPing();
                checkInactiveClients();
            }
        }
    }
}
