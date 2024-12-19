package server.commands.commands;

import server.model.ChatRoom;
import server.model.User;
import client.model.message.ClientMessage;
import server.MessageSender;
import server.commands.Command;

/**
 * The LoginCommand class handles the command for logging a user into the chat room.
 * It implements the {@link Command} interface and manages the process of adding a user to the chat room,
 * sending success messages, and broadcasting the user's entry into the room.
 */
public class LoginCommand implements Command {
    private final ChatRoom chatRoom;  // The chat room where users are logged in
    private final MessageSender messageSender;  // The MessageSender used to send messages to users

    /**
     * Constructs a LoginCommand that manages user login in the specified chat room.
     *
     * @param chatRoom The chat room where the user will be logged in.
     * @param messageSender The MessageSender used to send messages to users.
     */
    public LoginCommand(ChatRoom chatRoom, MessageSender messageSender) {
        this.chatRoom = chatRoom;
        this.messageSender = messageSender;
    }

    /**
     * Executes the login command. This method adds the user to the chat room if the username is available,
     * and sends appropriate success or error messages to the user.
     *
     * @param elements The arguments of the command, containing the username.
     * @param message The message that initiated the command.
     * @param owner The user who issued the command.
     */
    @Override
    public void execute(String[] elements, ClientMessage message, User owner) {
        // Try to add the user to the chat room
        if (!chatRoom.addUser(owner)) {
            sendErrorMessage(String.format("Username %s is not available", owner.getNick()), owner);
        } else {
            sendLoginSuccess(owner);
        }
    }

    /**
     * Sends a welcome message to the user upon successful login and also sends the message history
     * and a broadcast notification to other users in the chat room.
     *
     * @param owner The user who successfully logged in.
     */
    private void sendLoginSuccess(User owner) {
        messageSender.sendLoginMessageToUser("Welcome to the room", owner);  // Send welcome message to the user
        messageSender.sendHistoryToUser(chatRoom.getMessageHistory(), owner);  // Send chat history to the user
        messageSender.sendBroadcast(String.format("User %s entered the chat!", owner.getNick()), chatRoom, owner);  // Broadcast user's entry
    }

    /**
     * Sends an error message to the user if their login attempt fails (e.g., username already taken).
     *
     * @param message The error message to be sent.
     * @param owner The user who will receive the error message.
     */
    private void sendErrorMessage(String message, User owner) {
        messageSender.sendErrorToUser(message, owner);  // Send the error message to the user
    }
}


