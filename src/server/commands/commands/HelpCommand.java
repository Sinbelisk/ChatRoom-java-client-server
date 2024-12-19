package server.commands.commands;

import common.models.User;
import common.models.message.ClientMessage;
import server.MessageSender;
import server.commands.Command;

public class HelpCommand implements Command {
    private final MessageSender messageSender;

    public HelpCommand(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Override
    public void execute(String[] elements, ClientMessage message, User owner) {
        messageSender.sendInfoToUser(getCommandList(), owner);
    }

    private String getCommandList() {
        return """
                Available commands:
                - list: shows all players in the chat room
                - private (username) (message): send a private message to another user
                - exit: exit the chat room
                """;
    }
}