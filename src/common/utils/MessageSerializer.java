package common.utils;

import common.models.User;
import common.models.message.ClientMessage;
import common.models.message.MessageType;

import java.io.*;
import java.sql.Timestamp;

/**
 * A utility class for serializing and deserializing {@link ClientMessage} objects to and from byte arrays.
 * <p>
 * This class handles the serialization and deserialization of messages. The serialized message includes:
 * <ul>
 *     <li>Message type (1 byte).</li>
 *     <li>Message ID (String, preceded by its length as a short).</li>
 *     <li>Message content (String, preceded by its length as a short).</li>
 *     <li>Owner information (User object, serialized as a byte array).</li>
 *     <li>Timestamp (long representing milliseconds since epoch).</li>
 * </ul>
 * The deserialization process reverses this structure to reconstruct the original {@link ClientMessage} object.
 */
public class MessageSerializer {

    /**
     * Serializes a {@link ClientMessage} object into a byte array.
     * <p>
     * The serialization process includes:
     * <ul>
     *     <li>1 byte for the message type.</li>
     *     <li>Short (2 bytes) for the length of the message ID, followed by the ID string.</li>
     *     <li>Short (2 bytes) for the length of the message content, followed by the content string.</li>
     *     <li>4 bytes for the length of the serialized {@link User} object, followed by the serialized User byte array.</li>
     *     <li>8 bytes for the timestamp (long).</li>
     * </ul>
     *
     * @param clientMessage The {@link ClientMessage} object to serialize.
     * @return A byte array representing the serialized {@link ClientMessage}.
     * @throws IOException If an I/O error occurs during serialization.
     */
    public static byte[] serialize(ClientMessage clientMessage) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try (DataOutputStream dataStream = new DataOutputStream(byteStream)) {

            // Serialize message type
            dataStream.writeByte(clientMessage.getType().getValue());

            // Serialize message ID
            dataStream.writeShort(clientMessage.getId().length());
            dataStream.writeBytes(clientMessage.getId());

            // Serialize message content
            dataStream.writeShort(clientMessage.getContent().length());
            dataStream.writeBytes(clientMessage.getContent());

            // Serialize user (owner of the message)
            byte[] ownerBytes = UserSerializer.serialize(clientMessage.getOwner());
            dataStream.writeInt(ownerBytes.length);
            dataStream.write(ownerBytes);

            // Serialize timestamp
            TimestampSerializer.serialize(dataStream, clientMessage.getTime());

        }
        return byteStream.toByteArray();
    }

    /**
     * Deserializes a byte array into a {@link ClientMessage} object.
     * <p>
     * The byte array is expected to follow the format described in the {@link #serialize(ClientMessage)} method.
     * The deserialization process converts the byte array back into the original message structure.
     *
     * @param data The byte array representing the serialized {@link ClientMessage}.
     * @return The deserialized {@link ClientMessage} object.
     * @throws IOException If an I/O error occurs during deserialization.
     */
    public static ClientMessage deserialize(byte[] data) throws IOException {
        try (ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
             DataInputStream dataStream = new DataInputStream(byteStream)) {

            // Deserialize message type
            MessageType type = MessageType.fromValue(dataStream.readByte());

            // Deserialize message ID
            String id = readString(dataStream);

            // Deserialize message content
            String content = readString(dataStream);

            // Deserialize user (owner of the message)
            byte[] ownerBytes = new byte[dataStream.readInt()];
            dataStream.readFully(ownerBytes);
            User owner = UserSerializer.deserialize(ownerBytes);

            // Deserialize timestamp
            Timestamp time = TimestampSerializer.deserialize(dataStream);

            // Return a new Message object with the deserialized data
            return new ClientMessage(content, owner, type);
        }
    }

    /**
     * Helper method to read a string from a {@link DataInputStream}.
     * The string is expected to be prefixed by its length (a short value).
     *
     * @param dataStream The {@link DataInputStream} to read from.
     * @return The deserialized string.
     * @throws IOException If an I/O error occurs during reading.
     */
    private static String readString(DataInputStream dataStream) throws IOException {
        short length = dataStream.readShort();
        byte[] bytes = new byte[length];
        dataStream.readFully(bytes);
        return new String(bytes);
    }
}