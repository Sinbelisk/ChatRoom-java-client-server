package server;

import common.util.MessageUtil;
import common.UDPSocket;
import common.models.ChatRoom;
import common.models.User;
import common.models.message.ChatMessage;
import common.models.message.ClientMessage;
import server.commands.CommandHandler;

import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;


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
        this.commandHandler = new CommandHandler(chatRoom, messageSender);
    }

    public static void main(String[] args) throws Exception {
        new Server(6969).run();
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
            case ClientMessage.PONG -> handlePong(userKey);
            case ClientMessage.COMMAND -> handleCommand(clientMessage, user);
            case ClientMessage.MSG -> handleMessage(clientMessage, user);
            default -> log(Level.WARNING, "Unknown message type '%d' from user %s", clientMessage.getType(), userKey);
        }

        pingCounters.put(userKey, 0); // Reset ping counter on message receipt
    }

    private void handlePong(String userKey) {
        log(Level.INFO, "Pong received from user %s", userKey);
    }

    private void handleCommand(ClientMessage clientMessage, User user) {
        log(Level.INFO, "Command request from user %s: %s", user.getKey(), clientMessage.getContent());
        commandHandler.handleCommand(clientMessage, user);
    }

    private void handleMessage(ClientMessage clientMessage, User user) {
        if (!chatRoom.hasUser(user)) {
            log(Level.WARNING, "Received message from unregistered user %s", user.getKey());
            return;
        }
        ChatMessage msg = new ChatMessage(clientMessage.getContent(), user);
        chatRoom.saveMessage(msg);
        log(Level.INFO, "Broadcasting message from %s: %s", user.getKey(), msg.getFormattedContent());
        messageSender.sendBroadcast(msg.getFormattedContent(), chatRoom, user);
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
        log(Level.INFO, "Sending pings to inactive users...");
        for (User user : inactiveUsers) {
            String userKey = user.getKey();
            pingCounters.put(userKey, pingCounters.getOrDefault(userKey, 0) + 1);

            if (pingCounters.get(userKey) >= MAX_PING_COUNT) {
                handleInactiveUser(user);
            } else {
                messageSender.sendPing(user);
            }
        }
        log(Level.INFO, "Ping sending completed!");
    }

    private void handleInactiveUser(User user) {
        log(Level.WARNING, "User %s did not respond to pings and will be removed.", user.getKey());
        chatRoom.removeUser(user);
        pingCounters.remove(user.getKey());
        messageSender.sendBroadcast(String.format("User %s has been disconnected due to inactivity.", user.getNick()), chatRoom, null);
    }
}