package server;

import common.SimpleLogger;
import common.UDPOperation;
import common.models.ChatRoom;
import common.models.User;
import common.models.message.ChatMessage;
import common.models.message.ClientMessage;
import common.models.message.ServerMessage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandHandler {
    private final ChatRoom chatRoom;
    private final UDPOperation udpOperation;
    private final MessageSender messageSender;

    private static final Logger log = SimpleLogger.getInstance().getLogger(CommandHandler.class);

    public CommandHandler(ChatRoom chatRoom, UDPOperation udpOperation, MessageSender messageSender) {
        this.chatRoom = chatRoom;
        this.udpOperation = udpOperation;
        this.messageSender = messageSender;
        log.log(Level.INFO, "CommandHandler initialized.");
    }

    public void handleCommand(ClientMessage message, User owner) {
        String[] elements = message.getContent().split("\\s+");
        log.log(Level.INFO, "Handling command from user {0}: {1}", new Object[]{owner.getNick(), message.getContent()});

        switch (elements[0]) {
            case "login" -> handleLogin(owner);
            case "list" -> {
                log.log(Level.INFO, "User {0} requested the list of users.", owner.getNick());
                messageSender.sendInfoToUser(chatRoom.listUsers(), owner);
            }
            case "private" -> handlePrivateMessage(elements, message, owner);
            case "exit" -> handleExit(owner);
            case "help" -> {
                log.log(Level.INFO, "User {0} requested help.", owner.getNick());
                messageSender.sendInfoToUser(getCommandList(), owner);
            }
            default ->{
                log.log(Level.WARNING, "Unknown command from user {0}: {1}", new Object[]{owner.getNick(), message.getContent()});
                messageSender.sendInfoToUser(String.format("Command %s does not exist, use 'help' to view all commands", elements[0]), owner);
            }
        }
    }

    private void handleExit(User owner) {
        log.log(Level.INFO, "User {0} is exiting the chat.", owner.getNick());
        chatRoom.removeUser(owner);
        messageSender.sendExitMessageToUser("Connection terminated.", owner);
        messageSender.sendBroadcast(String.format("User %s left the chat!", owner.getNick()), chatRoom, owner);
        log.log(Level.INFO, "User {0} has exited the chat.", owner.getNick());
    }

    private void handlePrivateMessage(String[] elements, ClientMessage message, User owner) {
        if (elements.length < 3) {
            log.log(Level.WARNING, "Invalid private message format from user {0}.", owner.getNick());
            messageSender.sendErrorToUser("Invalid private message format. Use: private (username) (message)", owner);
            return;
        }

        String receiptNick = elements[1];
        User receipt = chatRoom.getUserByNick(receiptNick);

        if (receipt == null) {
            log.log(Level.WARNING, "User {0} attempted to send a private message to a non-existent user: {1}", new Object[]{owner.getNick(), receiptNick});
            messageSender.sendErrorToUser(String.format("User %s does not exist!", receiptNick), owner);
            return;
        }

        int index = message.getContent().indexOf(receiptNick);
        String privateMsg = message.getContent().substring(index + receiptNick.length()).trim();

        ChatMessage privateMessage = new ChatMessage(privateMsg, owner);
        messageSender.sendInfoToUser(privateMessage.getFormattedContentAsPrivate(), receipt);

        log.log(Level.INFO, "User {0} sent a private message to {1}.", new Object[]{owner.getNick(), receiptNick});
    }

    private void handleLogin(User owner) {
        log.log(Level.INFO, "User {0} is attempting to log in.", owner.getNick());
        if (!chatRoom.addUser(owner)) {
            log.log(Level.WARNING, "Login failed for user {0}: username is not available.", owner.getNick());
            messageSender.sendErrorToUser(String.format("Username %s is not available", owner.getNick()), owner);
        } else {
            log.log(Level.INFO, "User {0} logged in successfully.", owner.getNick());
            messageSender.sendLoginMessageToUser("Welcome to the room", owner);
            messageSender.sendHistoryToUser(chatRoom.getMessageHistory(), owner);
            messageSender.sendBroadcast(String.format("User %s entered the chat!", owner.getNick()), chatRoom, owner);
        }
    }

    private String getCommandList() {
        log.log(Level.FINE, "Providing command list.");
        return """
                Available commands:
                - list: shows all players in the chat room
                - private (username) (message): send a private message to another user
                - exit: exit the chat room
                """;
    }
}
