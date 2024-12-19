package server.commands;

import common.models.ChatRoom;
import common.models.User;
import common.models.message.ChatMessage;
import common.models.message.ClientMessage;
import server.MessageSender;

import java.util.Arrays;

public class PrivateCommand implements Command {
    private final ChatRoom chatRoom;
    private final MessageSender messageSender;

    public PrivateCommand(ChatRoom chatRoom, MessageSender messageSender) {
        this.chatRoom = chatRoom;
        this.messageSender = messageSender;
    }

    @Override
    public void execute(String[] elements, ClientMessage message, User owner) {
        if (elements.length < 3) {
            sendErrorMessage("Invalid private message format. Use: private (username) (message)", owner);
            return;
        }

        String receiptNick = elements[1];
        User receipt = chatRoom.getUserByNick(receiptNick);

        if (receipt == null) {
            sendErrorMessage(String.format("User %s does not exist!", receiptNick), owner);
            return;
        }

        String privateMsg = String.join(" ", Arrays.copyOfRange(elements, 2, elements.length));
        ChatMessage privateMessage = new ChatMessage(privateMsg, owner);
        messageSender.sendInfoToUser(privateMessage.getFormattedContentAsPrivate(), receipt);
    }

    private void sendErrorMessage(String message, User owner) {
        messageSender.sendErrorToUser(message, owner);
    }
}

