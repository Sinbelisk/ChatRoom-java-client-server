package server;

import common.MessageUtil;
import common.UDPSocket;
import common.models.ChatRoom;
import common.models.User;
import common.models.message.ChatMessage;
import common.models.message.ClientMessage;
import common.models.message.ServerMessage;

public class CommandHandler {
    private final ChatRoom chatRoom;
    private final UDPSocket udpSocket;

    public CommandHandler(ChatRoom chatRoom, UDPSocket udpSocket) {
        this.chatRoom = chatRoom;
        this.udpSocket = udpSocket;
    }

    public void handleCommand(ClientMessage message, User owner) {
        String[] elements = message.getContent().split("\\s+");

        switch (elements[0]) {
            case "login" -> handleLogin(owner);
            case "list" -> sendMessage(new ServerMessage(chatRoom.listUsers(), ServerMessage.ServerStatus.INFO.getValue()), owner);
            case "private" -> handlePrivateMessage(elements, message, owner);
        }
    }

    private void sendMessage(ServerMessage message, User user) {
        byte[] msgData = MessageUtil.createServerMessage(message);
        udpSocket.send(msgData, user.getIp(), user.getPort());
    }

    private void handlePrivateMessage(String[] elements, ClientMessage message, User owner) {
        User receipt = chatRoom.getUserByNick(elements[1]);
        String receiptNick = elements[1];
        if (receipt == null) {
            sendMessage(new ServerMessage("That user does not exist", ServerMessage.ServerStatus.ERROR.getValue()), owner);
            return;
        }
        int index = message.getContent().indexOf(receiptNick);
        String privateMsg = message.getContent().substring(index + receiptNick.length()).trim();
        ChatMessage chatmsg = new ChatMessage(privateMsg, owner);
        sendMessage(new ServerMessage(chatmsg.getFormattedContentAsPrivate(), ServerMessage.ServerStatus.INFO.getValue()), receipt);
    }

    private void handleLogin(User owner) {
        if (!chatRoom.addUser(owner)) {
            sendMessage(new ServerMessage("Username not available", ServerMessage.ServerStatus.ERROR.getValue()), owner);
        } else {
            sendMessage(new ServerMessage("Welcome to the room", ServerMessage.ServerStatus.LOGIN_OK.getValue()), owner);
        }
    }
}

