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
import java.util.Set;
import java.util.logging.Level;

import java.util.Iterator;


public class Server extends UDPSocket {
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private static final int MAX_PING_COUNT = 3;
    private final ChatRoom chatRoom = new ChatRoom();
    private final CommandHandler commandHandler;
    private final MessageSender messageSender;
    private final Map<String, Integer> pingCounters = new HashMap<>();

    public Server(int port) throws SocketException {
        super(port, DEFAULT_BUFFER_SIZE);
        this.messageSender = new MessageSender(this);
        this.commandHandler = new CommandHandler(chatRoom, this, messageSender);
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(6969);
        server.run();
    }

    @Override
    public void processPacket(DatagramPacket packet) {
        ClientMessage clientMessage = MessageUtil.parseClientMessage(packet.getData(), packet.getLength());

        if (clientMessage == null) {
            log(Level.SEVERE, "Error, a null or wrong packet arrived");
            return;
        }

        User user = new User(clientMessage.getNick(), packet.getAddress(), packet.getPort());
        chatRoom.updateActivity(user);

        String userKey = user.getKey();
        switch (clientMessage.getType()) {
            case ClientMessage.PONG -> log(Level.INFO, "Pong received from user %s", user.getKey());
            case ClientMessage.COMMAND -> {
                log(Level.INFO, "Command request from user %s: %s", userKey, clientMessage.getContent());
                commandHandler.handleCommand(clientMessage, user);
            }
            default -> {
                ChatMessage processedMessage = new ChatMessage(clientMessage.getContent(), user);
                log(Level.INFO, "Message received from client %s: %s", userKey, processedMessage.toString());
                handleChatMessage(processedMessage);
            }
        }

        pingCounters.put(userKey, 0);
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

    public void run() {
        log(Level.INFO, "Server is running and ready to receive packets.");
        while (true) {
            receive();
            sendPingsAndCheckTimeouts();
        }
    }

    private void sendPingsAndCheckTimeouts() {
        Set<User> inactiveUsers = chatRoom.getInactiveUsers();

        for (User user : inactiveUsers) {
            String userKey = user.getKey();

            // Increments the ping counter of the user.
            pingCounters.put(userKey, pingCounters.getOrDefault(userKey, 0) + 1);

            // If the user exceeds the max number of pings, it is removed from the room
            if (pingCounters.get(userKey) >= MAX_PING_COUNT) {
                log(Level.WARNING, "User %s did not respond to pings and will be removed.", userKey);
                chatRoom.removeUser(user);
                pingCounters.remove(userKey);
                messageSender.sendBroadcast(String.format("User %s has been disconnected due to inactivity.", user.getNick()), chatRoom, null);
            } else {
                // Send ping to user
                log(Level.INFO, "Ping sent to user %s", userKey);
                messageSender.sendPing(user);
            }
        }
    }
}
