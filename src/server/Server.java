package server;

import common.MessageUtil;
import common.UDPSocket;
import common.models.ChatRoom;
import common.models.message.*;
import common.models.User;

import java.net.DatagramPacket;
import java.net.SocketException;

public class Server extends UDPSocket {
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private final ChatRoom chatRoom = new ChatRoom();
    private final CommandHandler commandHandler;
    private final MessageSender messageSender;

    public Server(int port) throws SocketException {
        super(port, DEFAULT_BUFFER_SIZE);
        this. commandHandler = new CommandHandler(chatRoom, this);
        this.messageSender = new MessageSender(this);
    }
    public static void main(String[] args) throws Exception {
        Server server = new Server(6969);
        while (true) {
            server.receive();
        }
    }
    @Override
    public void processPacket(DatagramPacket packet) {
        ClientMessage clientMessage = MessageUtil.parseClientMessage(packet.getData(), packet.getLength());

        if (clientMessage == null) {
            return;
        }

        User user = new User(clientMessage.getNick(), packet.getAddress(), packet.getPort());

        if (clientMessage.getType() == ClientMessage.COMMAND) {
            commandHandler.handleCommand(clientMessage, user);
        } else {
            ChatMessage processedMessage = new ChatMessage(clientMessage.getContent(), user);
            handleChatMessage(processedMessage);
        }
    }

    private void handleChatMessage(ChatMessage msg) {
        if (!chatRoom.hasUser(msg.getOwner())) {
            return;
        }
        chatRoom.saveMessage(msg);

        ServerMessage message = new ServerMessage(msg.getFormattedContent(), ServerMessage.ServerStatus.INFO.getValue());
        messageSender.sendBroadcast(message,chatRoom, msg.getOwner());
    }
}
