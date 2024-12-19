package server.commands;

import common.models.ChatRoom;
import common.models.User;
import common.models.message.ClientMessage;
import server.MessageSender;

public class LoginCommand implements Command {
    private final ChatRoom chatRoom;
    private final MessageSender messageSender;

    public LoginCommand(ChatRoom chatRoom, MessageSender messageSender) {
        this.chatRoom = chatRoom;
        this.messageSender = messageSender;
    }

    @Override
    public void execute(String[] elements, ClientMessage message, User owner) {
        if (!chatRoom.addUser(owner)) {
            sendErrorMessage(String.format("Username %s is not available", owner.getNick()), owner);
        } else {
            sendLoginSuccess(owner);
        }
    }

    private void sendLoginSuccess(User owner) {
        messageSender.sendLoginMessageToUser("Welcome to the room", owner);
        messageSender.sendHistoryToUser(chatRoom.getMessageHistory(), owner);
        messageSender.sendBroadcast(String.format("User %s entered the chat!", owner.getNick()), chatRoom, owner);
    }

    private void sendErrorMessage(String message, User owner) {
        messageSender.sendErrorToUser(message, owner);
    }
}

