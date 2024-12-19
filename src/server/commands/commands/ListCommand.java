package server.commands.commands;

import common.models.ChatRoom;
import common.models.User;
import common.models.message.ClientMessage;
import server.MessageSender;
import server.commands.Command;

public class ListCommand implements Command {
    private final ChatRoom chatRoom;
    private final MessageSender messageSender;

    public ListCommand(ChatRoom chatRoom, MessageSender messageSender) {
        this.chatRoom = chatRoom;
        this.messageSender = messageSender;
    }

    @Override
    public void execute(String[] elements, ClientMessage message, User owner) {
        messageSender.sendInfoToUser(chatRoom.listUsers(), owner);
    }
}

