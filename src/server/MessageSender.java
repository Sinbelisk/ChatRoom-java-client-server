package server;

import common.MessageUtil;
import common.UDPSocket;
import common.models.ChatRoom;
import common.models.User;
import common.models.message.ServerMessage;

public class MessageSender {
    private final UDPSocket udpSocket;

    // Constructor que recibe una referencia a UDPSocket (como Server)
    public MessageSender(UDPSocket udpSocket) {
        this.udpSocket = udpSocket;  // Guardamos la referencia del UDPSocket
    }

    // Enviar un mensaje a un único usuario
    public void sendToUser(ServerMessage message, User user) {
        byte[] msgData = MessageUtil.createServerMessage(message);  // Convertimos el mensaje en un byte[]
        udpSocket.send(msgData, user.getIp(), user.getPort());  // Enviamos el mensaje al usuario especificado
    }

    // Enviar un mensaje de broadcast a todos los usuarios (menos al remitente)
    public void sendBroadcast(ServerMessage message, ChatRoom chatRoom, User owner) {
        byte[] msgData = MessageUtil.createServerMessage(message);  // Convertimos el mensaje en un byte[]

        // Enviamos el mensaje a todos los usuarios del chatRoom excepto al dueño (remitente)
        chatRoom.getUsers().stream()
                .filter(user -> !user.equals(owner))
                .forEach(user -> udpSocket.send(msgData, user.getIp(), user.getPort()));  // Enviamos el mensaje
    }

    // Enviar el historial de mensajes a un usuario
    public void sendHistoryToUser(String messageHistory, User user) {
        ServerMessage historyMessage = new ServerMessage(messageHistory, ServerMessage.ServerStatus.INFO.getValue());
        sendToUser(historyMessage, user);  // Usamos el método para enviar al usuario
    }

    // Enviar mensaje privado a un usuario
    public void sendPrivateMessage(String privateMsg, User recipient, User owner) {
        ServerMessage privateMessage = new ServerMessage(privateMsg, ServerMessage.ServerStatus.INFO.getValue());
        sendToUser(privateMessage, recipient);  // Usamos el método para enviar al usuario
    }
}


