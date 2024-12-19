package server.commands.commands;

import server.model.ChatRoom;
import server.model.User;
import client.model.message.ClientMessage;
import server.MessageSender;
import server.commands.Command;

/**
 * The ListCommand class handles the command for listing all users currently in the chat room.
 * It implements the {@link Command} interface and is responsible for retrieving the list of users
 * in the chat room and sending it to the requesting user.
 */
public class ListCommand implements Command {
    private final ChatRoom chatRoom;  // The chat room from which to list users
    private final MessageSender messageSender;  // The MessageSender used to send messages to users

    /**
     * Constructs a ListCommand that retrieves and lists users from the specified chat room.
     *
     * @param chatRoom The chat room where the users are listed.
     * @param messageSender The MessageSender used to send messages to users.
     */
    public ListCommand(ChatRoom chatRoom, MessageSender messageSender) {
        this.chatRoom = chatRoom;
        this.messageSender = messageSender;
    }

    /**
     * Executes the list command. This method retrieves the list of users in the chat room
     * and sends the list to the user who issued the command.
     *
     * @param elements The arguments of the command (not used in this case).
     * @param message The message that initiated the command (not used in this case).
     * @param owner The user who issued the command.
     */
    @Override
    public void execute(String[] elements, ClientMessage message, User owner) {
        messageSender.sendInfoToUser(chatRoom.listUsers(), owner);  // Send the list of users to the requesting user
    }
}

