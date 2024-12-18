package common;

import common.models.message.ClientMessage;
import common.models.message.ServerMessage;

public class MessageUtil {

    // Crear un mensaje de cliente en formato String
    public static byte[] createClientMessage(ClientMessage message) {
        return ("CLIENT|" + message.getNick() + "|" + message.getType() + "|" + message.getContent()).getBytes();
    }

    // Crear un mensaje de servidor en formato String
    public static byte[] createServerMessage(ServerMessage message) {
        return ("SERVER|" + message.getStatus().getValue() + "|" + message.getContent()).getBytes();
    }

    // Parsear un mensaje de cliente desde una cadena String
    public static ClientMessage parseClientMessage(byte[] data){
        return parseClientMessage(new String(data));
    }
    public static ClientMessage parseClientMessage(String message) {
        String[] parts = message.split("\\|");
        if (parts.length == 4 && parts[0].equals("CLIENT")) {
            String nick = parts[1];
            int type = Integer.parseInt(parts[2]);
            String content = parts[3];
            return new ClientMessage(content, nick, type);
        }
        return null;
    }

    public static ServerMessage parseServerMessage(byte[] data){
        return parseServerMessage(new String(data));
    }

    // Parsear un mensaje de servidor desde una cadena String
    public static ServerMessage parseServerMessage(String message) {
        String[] parts = message.split("\\|");

        if (parts.length == 3 && parts[0].equals("SERVER")) {
            int status = Integer.parseInt(parts[1]);
            String content = parts[2];
            return new ServerMessage(content, status);
        }
        return null;
    }
}

