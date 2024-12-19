package server.commands.commands;

import server.model.User;
import client.model.message.ClientMessage;
import server.MessageSender;
import server.commands.Command;

/**
 * The HelpCommand class handles the command to display the list of available commands
 * to the user. It implements the {@link Command} interface and is responsible for sending
 * a help message containing descriptions of the available commands to the requesting user.
 */
public class HelpCommand implements Command {
    private final MessageSender messageSender;  // The MessageSender used to send messages to users

    /**
     * Constructs a HelpCommand that will send the list of available commands to the user.
     *
     * @param messageSender The MessageSender used to send messages to users.
     */
    public HelpCommand(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    /**
     * Executes the help command. This method sends a list of available commands to the user
     * who issued the command.
     *
     * @param elements The arguments of the command (not used in this case).
     * @param message The message that initiated the command (not used in this case).
     * @param owner The user who issued the command.
     */
    @Override
    public void execute(String[] elements, ClientMessage message, User owner) {
        messageSender.sendInfoToUser(getCommandList(), owner);  // Send the list of available commands to the requesting user
    }

    /**
     * Returns a string representing the list of available commands and their descriptions.
     *
     * @return A formatted string containing the list of available commands.
     */
    private String getCommandList() {
        return """
                Available commands:
                - list: shows all players in the chat room
                - private (username) (message): send a private message to another user
                - exit: exit the chat room
                """;
    }
}
