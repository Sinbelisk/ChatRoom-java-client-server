package server.commands;

import common.util.SimpleLogger;
import common.models.ChatRoom;
import common.models.User;
import common.models.message.ClientMessage;
import server.MessageSender;
import server.commands.commands.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandHandler {
    private final MessageSender messageSender;
    private static final Logger log = SimpleLogger.getInstance().getLogger(CommandHandler.class);

    private final Map<String, Command> commandMap;

    public CommandHandler(ChatRoom chatRoom, MessageSender messageSender) {
        this.messageSender = messageSender;
        this.commandMap = new HashMap<>();

        // Inicializar los comandos
        commandMap.put("login", new LoginCommand(chatRoom, messageSender));
        commandMap.put("list", new ListCommand(chatRoom, messageSender));
        commandMap.put("private", new PrivateCommand(chatRoom, messageSender));
        commandMap.put("exit", new ExitCommand(chatRoom, messageSender));
        commandMap.put("help", new HelpCommand(messageSender));

        log.log(Level.INFO, "CommandHandler initialized.");
    }

    public void handleCommand(ClientMessage message, User owner) {
        String[] elements = message.getContent().split("\\s+");
        log.log(Level.INFO, "Handling command from user {0}: {1}", new Object[]{owner.getNick(), message.getContent()});

        if (elements.length == 0) {
            messageSender.sendErrorToUser("Command cannot be empty", owner);
            return;
        }

        String command = elements[0].toLowerCase();
        Command cmd = commandMap.get(command);

        if (cmd != null) {
            cmd.execute(elements, message, owner);
        } else {
            handleUnknownCommand(command, owner);
        }
    }

    private void handleUnknownCommand(String command, User owner) {
        log.log(Level.WARNING, "Unknown command from user {0}: {1}", new Object[]{owner.getNick(), command});
        messageSender.sendErrorToUser(String.format("Command '%s' does not exist. Use 'help' to view all commands.", command), owner);
    }
}