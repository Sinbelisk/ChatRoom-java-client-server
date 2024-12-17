package common.utils;

import common.models.message.ServerMessage;
import common.models.message.ServerStatus;

import java.io.*;

/**
 * This class provides utility methods to serialize and deserialize {@link ServerMessage} objects.
 */
public class ServerMessageSerializer {

    /**
     * Serializes a {@link ServerMessage} object into a byte array.
     *
     * @param serverMessage The {@link ServerMessage} object to serialize.
     * @return A byte array representing the serialized {@link ServerMessage}.
     * @throws IOException If an I/O error occurs during serialization.
     */
    public static byte[] serialize(ServerMessage serverMessage) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try (DataOutputStream dataStream = new DataOutputStream(byteStream)) {

            // Serialize content (String)
            String content = serverMessage.getContent();
            dataStream.writeShort(content.length());  // Length of the content (2 bytes)
            dataStream.writeBytes(content);           // Content itself

            // Serialize status (ServerStatus enum)
            dataStream.writeByte(serverMessage.getStatus().getValue());  // Status value (1 byte)
        }
        return byteStream.toByteArray();
    }

    /**
     * Deserializes a byte array into a {@link ServerMessage} object.
     *
     * @param data The byte array containing the serialized {@link ServerMessage}.
     * @return The deserialized {@link ServerMessage} object.
     * @throws IOException If an I/O error occurs during deserialization.
     */
    public static ServerMessage deserialize(byte[] data) throws IOException {
        try (ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
             DataInputStream dataStream = new DataInputStream(byteStream)) {

            // Deserialize content (String)
            String content = readString(dataStream);

            // Deserialize status (ServerStatus enum)
            byte statusByte = dataStream.readByte();
            ServerStatus status = ServerStatus.fromValue(statusByte);

            return new ServerMessage(content, status);
        }
    }

    /**
     * Helper method to read a String from the DataInputStream.
     *
     * @param dataStream The DataInputStream to read from.
     * @return The deserialized String.
     * @throws IOException If an I/O error occurs while reading.
     */
    private static String readString(DataInputStream dataStream) throws IOException {
        short length = dataStream.readShort();
        byte[] bytes = new byte[length];
        dataStream.readFully(bytes);
        return new String(bytes);
    }
}

