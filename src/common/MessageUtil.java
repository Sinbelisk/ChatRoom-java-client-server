package common;

import common.models.message.ClientMessage;
import common.models.message.ServerMessage;

/**
 * Utility class for handling client and server messages in a UDP-based chat application.
 * This class provides methods to create and parse messages from both clients and servers.
 * It ensures consistent encoding and proper handling of message lengths.
 */
public class MessageUtil {

    /**
     * Creates a client message in the form of a byte array.
     *
     * @param message The ClientMessage object containing the message details.
     * @return A byte array representing the client message in UTF-8 encoding.
     */
    public static byte[] createClientMessage(ClientMessage message) {
        String formattedMessage = "CLIENT|" + message.getNick() + "|" + message.getType() + "|" + message.getContent();
        return formattedMessage.getBytes();
    }

    /**
     * Creates a server message in the form of a byte array.
     *
     * @param message The ServerMessage object containing the message details.
     * @return A byte array representing the server message in UTF-8 encoding.
     */
    public static byte[] createServerMessage(ServerMessage message) {
        String formattedMessage = "SERVER|" + message.getStatus().getValue() + "|" + message.getContent();
        return formattedMessage.getBytes();
    }

    /**
     * Parses a client message from a byte array with a specified length.
     *
     * @param data   The byte array containing the message data.
     * @param length The actual length of the data to parse.
     * @return A ClientMessage object parsed from the data, or null if the format is invalid.
     */
    public static ClientMessage parseClientMessage(byte[] data, int length) {
        String rawMessage = new String(data, 0, length);
        return parseClientMessage(rawMessage);
    }

    /**
     * Parses a client message from a string.
     *
     * @param message The string representation of the client message.
     * @return A ClientMessage object parsed from the string, or null if the format is invalid.
     */
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

    /**
     * Parses a server message from a byte array with a specified length.
     *
     * @param data   The byte array containing the message data.
     * @param length The actual length of the data to parse.
     * @return A ServerMessage object parsed from the data, or null if the format is invalid.
     */
    public static ServerMessage parseServerMessage(byte[] data, int length) {
        String rawMessage = new String(data, 0, length);
        return parseServerMessage(rawMessage);
    }

    /**
     * Parses a server message from a string.
     *
     * @param message The string representation of the server message.
     * @return A ServerMessage object parsed from the string, or null if the format is invalid.
     */
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