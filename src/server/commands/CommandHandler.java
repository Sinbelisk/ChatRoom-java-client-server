package server.commands;

import common.util.SimpleLogger;
import server.model.ChatRoom;
import server.model.User;
import client.model.message.ClientMessage;
import server.MessageSender;
import server.commands.commands.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The CommandHandler class is responsible for managing and executing commands issued by users in a chat room.
 * It handles the parsing of the command, selects the appropriate Command implementation, and executes it.
 */
public class CommandHandler {
    private final MessageSender messageSender;  // The MessageSender used to send messages to users.
    private static final Logger log = SimpleLogger.getInstance().getLogger(CommandHandler.class);  // Logger for logging events.

    private final Map<String, Command> commandMap;  // Map to store available commands and their implementations.

    /**
     * Constructs a CommandHandler that initializes the available commands and their associated actions.
     *
     * @param chatRoom The chat room where the commands are executed.
     * @param messageSender The MessageSender used to send messages to users.
     */
    public CommandHandler(ChatRoom chatRoom, MessageSender messageSender) {
        this.messageSender = messageSender;
        this.commandMap = new HashMap<>();

        // Initialize the commands with their corresponding implementations
        commandMap.put("login", new LoginCommand(chatRoom, messageSender));
        commandMap.put("list", new ListCommand(chatRoom, messageSender));
        commandMap.put("private", new PrivateCommand(chatRoom, messageSender));
        commandMap.put("exit", new ExitCommand(chatRoom, messageSender));
        commandMap.put("help", new HelpCommand(messageSender));

        log.log(Level.INFO, "CommandHandler initialized.");
    }

    /**
     * Handles a command received from a user.
     * It parses the message, determines the command type, and executes the corresponding command.
     *
     * @param message The message that contains the command to be executed.
     * @param owner The user who issued the command.
     */
    public void handleCommand(ClientMessage message, User owner) {
        String[] elements = message.getContent().split("\\s+");
        log.log(Level.INFO, "Handling command from user {0}: {1}", new Object[]{owner.getNick(), message.getContent()});

        if (elements.length == 0) {
            messageSender.sendErrorToUser("Command cannot be empty", owner);
            return;
        }

        String command = elements[0].toLowerCase();  // Extract the command (first element)
        Command cmd = commandMap.get(command);  // Retrieve the corresponding Command implementation

        if (cmd != null) {
            cmd.execute(elements, message, owner);  // Execute the command if found
        } else {
            handleUnknownCommand(command, owner);  // Handle unknown command
        }
    }

    /**
     * Handles an unknown command by notifying the user that the command does not exist.
     *
     * @param command The unknown command that the user attempted to execute.
     * @param owner The user who issued the unknown command.
     */
    private void handleUnknownCommand(String command, User owner) {
        log.log(Level.WARNING, "Unknown command from user {0}: {1}", new Object[]{owner.getNick(), command});
        messageSender.sendErrorToUser(String.format("Command '%s' does not exist. Use 'help' to view all commands.", command), owner);
    }
}