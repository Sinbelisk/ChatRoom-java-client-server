package server;

import common.MessageUtil;
import common.UDPSocket;
import common.models.ChatRoom;
import common.models.User;
import common.models.message.ChatMessage;
import common.models.message.ServerMessage;

public class CommandHandler {
    private final ChatRoom chatRoom;
    private final UDPSocket udpSocket;

    public CommandHandler(ChatRoom chatRoom, UDPSocket udpSocket) {
        this.chatRoom = chatRoom;
        this.udpSocket = udpSocket;
    }

    public void handleCommand(ChatMessage message) {
        User owner = message.getOwner();
        String[] elements = message.getContent().split("\\s+");

        switch (elements[0]) {
            case "login": handleLogin(owner); break;
            case "list": sendMessage(new ServerMessage(chatRoom.listUsers(), ServerMessage.ServerStatus.INFO.getValue()), owner); break;
            case "private": handlePrivateMessage(elements, message, owner); break;
        }
    }

    private void sendMessage(ServerMessage message, User user) {
        byte[] msgData = MessageUtil.createServerMessage(message);
        udpSocket.send(msgData, user.getIp(), user.getPort());  // Usamos la referencia udpSocket
    }

    private void handlePrivateMessage(String[] elements, ChatMessage message, User owner) {
        User whisperUser = chatRoom.getUserByNick(elements[1]);
        if (whisperUser == null) {
            sendMessage(new ServerMessage("That user does not exist", ServerMessage.ServerStatus.ERROR.getValue()), owner);
            return;
        }
        String privateMsg = message.getContent().substring(elements[1].length()).trim();
        sendMessage(new ServerMessage(privateMsg, ServerMessage.ServerStatus.INFO.getValue()), whisperUser);
    }

    private void handleLogin(User owner) {
        if (!chatRoom.addUser(owner)) {
            sendMessage(new ServerMessage("Username not available", ServerMessage.ServerStatus.ERROR.getValue()), owner);
        } else {
            sendMessage(new ServerMessage("Welcome to the room", ServerMessage.ServerStatus.LOGIN_OK.getValue()), owner);
        }
    }
}

