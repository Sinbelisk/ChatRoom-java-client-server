package server.commands.commands;

import server.model.ChatRoom;
import server.model.User;
import client.model.message.ClientMessage;
import server.MessageSender;
import server.commands.Command;

/**
 * The ExitCommand class handles the command to allow a user to exit the chat room.
 * It implements the {@link Command} interface and is responsible for removing the user
 * from the chat room, sending an exit message to the user, and notifying other users
 * that the user has left the chat.
 */
public class ExitCommand implements Command {
    private final ChatRoom chatRoom;  // The chat room where users are managed
    private final MessageSender messageSender;  // The MessageSender used to send messages to users

    /**
     * Constructs an ExitCommand that will handle the user's exit from the chat room.
     *
     * @param chatRoom The chat room from which the user will exit.
     * @param messageSender The MessageSender used to send messages to users.
     */
    public ExitCommand(ChatRoom chatRoom, MessageSender messageSender) {
        this.chatRoom = chatRoom;
        this.messageSender = messageSender;
    }

    /**
     * Executes the exit command. This method removes the user from the chat room,
     * sends an exit message to the user, and broadcasts a message to all other users
     * that the user has left the chat room.
     *
     * @param elements The arguments of the command (not used in this case).
     * @param message The message that initiated the command (not used in this case).
     * @param owner The user who issued the command (the user exiting the chat).
     */
    @Override
    public void execute(String[] elements, ClientMessage message, User owner) {
        chatRoom.removeUser(owner);  // Remove the user from the chat room
        messageSender.sendExitMessageToUser("Connection terminated.", owner);  // Send exit message to the user
        messageSender.sendBroadcast(String.format("User %s left the chat!", owner.getNick()), chatRoom, owner);  // Notify all other users
    }
}


