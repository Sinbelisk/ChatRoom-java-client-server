package server.commands.commands;

import server.model.ChatRoom;
import server.model.User;
import server.model.message.ChatMessage;
import client.model.message.ClientMessage;
import server.MessageSender;
import server.commands.Command;

import java.util.Arrays;

/**
 * The PrivateCommand class represents the command for sending private messages between users in a chat room.
 * It implements the {@link Command} interface and handles the process of sending a private message to a specific user.
 */
public class PrivateCommand implements Command {
    private final ChatRoom chatRoom;  // The chat room in which users interact.
    private final MessageSender messageSender;  // The MessageSender used to send messages to users.

    /**
     * Constructs a PrivateCommand that sends private messages to users within the specified chat room.
     *
     * @param chatRoom The chat room where the private messages are sent.
     * @param messageSender The MessageSender used to send the private messages.
     */
    public PrivateCommand(ChatRoom chatRoom, MessageSender messageSender) {
        this.chatRoom = chatRoom;
        this.messageSender = messageSender;
    }

    /**
     * Executes the private message command. This method checks if the private message format is correct,
     * validates the recipient, and sends the private message if all conditions are met.
     *
     * @param elements The arguments of the command, including the recipient username and message content.
     * @param message The message that initiated the command.
     * @param owner The user who issued the command.
     */
    @Override
    public void execute(String[] elements, ClientMessage message, User owner) {
        // Check if the private message format is valid
        if (elements.length < 3) {
            sendErrorMessage("Invalid private message format. Use: private (username) (message)", owner);
            return;
        }

        String receiptNick = elements[1];  // Get the recipient's nickname from the command arguments
        User receipt = chatRoom.getUserByNick(receiptNick);  // Find the recipient user in the chat room

        // Check if the recipient exists
        if (receipt == null) {
            sendErrorMessage(String.format("User %s does not exist!", receiptNick), owner);
            return;
        }

        // Construct the private message
        String privateMsg = String.join(" ", Arrays.copyOfRange(elements, 2, elements.length));
        ChatMessage privateMessage = new ChatMessage(privateMsg, owner);  // Create the private chat message
        messageSender.sendInfoToUser(privateMessage.getFormattedContentAsPrivate(), receipt);  // Send the message to the recipient
    }

    /**
     * Sends an error message to the user who initiated the command.
     *
     * @param message The error message to be sent.
     * @param owner The user who will receive the error message.
     */
    private void sendErrorMessage(String message, User owner) {
        messageSender.sendErrorToUser(message, owner);  // Send the error message to the user
    }
}


