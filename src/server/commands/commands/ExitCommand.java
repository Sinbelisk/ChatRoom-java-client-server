package server.commands.commands;

import common.models.ChatRoom;
import common.models.User;
import common.models.message.ClientMessage;
import server.MessageSender;
import server.commands.Command;

public class ExitCommand implements Command {
    private final ChatRoom chatRoom;
    private final MessageSender messageSender;

    public ExitCommand(ChatRoom chatRoom, MessageSender messageSender) {
        this.chatRoom = chatRoom;
        this.messageSender = messageSender;
    }

    @Override
    public void execute(String[] elements, ClientMessage message, User owner) {
        chatRoom.removeUser(owner);
        messageSender.sendExitMessageToUser("Connection terminated.", owner);
        messageSender.sendBroadcast(String.format("User %s left the chat!", owner.getNick()), chatRoom, owner);
    }
}

