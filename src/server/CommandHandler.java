package server;

import common.UDPOperation;
import common.models.ChatRoom;
import common.models.User;
import common.models.message.ChatMessage;
import common.models.message.ClientMessage;
import common.models.message.ServerMessage;

public class CommandHandler {
    private final ChatRoom chatRoom;
    private final UDPOperation udpOperation;
    private final MessageSender messageSender;

    public CommandHandler(ChatRoom chatRoom, UDPOperation udpOperation, MessageSender messageSender) {
        this.chatRoom = chatRoom;
        this.udpOperation = udpOperation;
        this.messageSender = messageSender;
    }

    public void handleCommand(ClientMessage message, User owner) {
        String[] elements = message.getContent().split("\\s+");

        switch (elements[0]) {
            case "login" -> handleLogin(owner);
            case "list" -> messageSender.sendToUser(new ServerMessage(chatRoom.listUsers(), ServerMessage.ServerStatus.INFO.getValue()), owner);
            case "private" -> handlePrivateMessage(elements, message, owner);
            case "help" -> messageSender.sendInfoToUser(getCommandList(), owner);
        }
    }

    private void handlePrivateMessage(String[] elements, ClientMessage message, User owner) {
        User receipt = chatRoom.getUserByNick(elements[1]);
        String receiptNick = elements[1];
        if (receipt == null) {
            messageSender.sendToUser(new ServerMessage("That user does not exist", ServerMessage.ServerStatus.ERROR.getValue()), owner);
            return;
        }
        int index = message.getContent().indexOf(receiptNick);
        String privateMsg = message.getContent().substring(index + receiptNick.length()).trim();

        ChatMessage privateMessage = new ChatMessage(privateMsg, owner);
        messageSender.sendToUser(new ServerMessage(privateMessage.getFormattedContentAsPrivate(), ServerMessage.ServerStatus.INFO.getValue()), receipt);
    }

    private void handleLogin(User owner) {
        if (!chatRoom.addUser(owner)) {
            messageSender.sendToUser(new ServerMessage("Username not available", ServerMessage.ServerStatus.ERROR.getValue()), owner);
        } else {
            messageSender.sendToUser(new ServerMessage("Welcome to the room", ServerMessage.ServerStatus.LOGIN_OK.getValue()), owner);
            messageSender.sendHistoryToUser(chatRoom.getMessageHistory(), owner);
        }
    }

    private String getCommandList(){
        return """
                Available commands:
                - list: shows all players in the chat room
                - private (username) (message): send a private message to another user
                - exit: exit the chat room
                """;
    }
}

