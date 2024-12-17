package common.utils;

import common.models.User;

import java.io.*;
import java.net.InetAddress;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * A utility class for serializing and deserializing {@link User} objects to and from byte arrays.
 */
public class UserSerializer {

    /**
     * Serializes a {@link User} object into a byte array.
     * <p>
     * The byte array format includes:
     * <ul>
     *     <li>Short (2 bytes) representing the length of the user's nickname.</li>
     *     <li>Nickname (String) of the user.</li>
     *     <li>4 bytes representing the user's IP address.</li>
     *     <li>4 bytes representing the user's port number.</li>
     * </ul>
     *
     * @param user The user object to serialize.
     * @return A byte array representing the serialized user.
     * @throws IOException If an I/O error occurs during serialization.
     */
    public static byte[] serialize(User user) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try (DataOutputStream dataStream = new DataOutputStream(byteStream)) {

            // Serialize the nickname (String)
            dataStream.writeShort(user.getNick().length());
            dataStream.writeBytes(user.getNick());

            // Serialize the IP address (4 bytes)
            byte[] ipBytes = user.getIp().getAddress();
            dataStream.write(ipBytes);

            // Serialize the port (4 bytes)
            dataStream.writeInt(user.getPort());

        }
        return byteStream.toByteArray();
    }

    /**
     * Deserializes a byte array into a {@link User} object.
     * <p>
     * The expected byte array format is as follows:
     * <ul>
     *     <li>Short (2 bytes) representing the length of the user's nickname.</li>
     *     <li>Nickname (String) of the user.</li>
     *     <li>4 bytes representing the user's IP address.</li>
     *     <li>4 bytes representing the user's port number.</li>
     * </ul>
     *
     * @param data The byte array representing the serialized user.
     * @return A {@link User} object deserialized from the byte array.
     * @throws IOException If an I/O error occurs during deserialization.
     */
    public static User deserialize(byte[] data) throws IOException {
        try (ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
             DataInputStream dataStream = new DataInputStream(byteStream)) {

            // Deserialize the nickname
            short nickLength = dataStream.readShort();
            byte[] nickBytes = new byte[nickLength];
            dataStream.readFully(nickBytes);
            String nick = new String(nickBytes);

            // Deserialize the IP address (4 bytes)
            byte[] ipBytes = new byte[4];
            dataStream.readFully(ipBytes);
            InetAddress ip = InetAddress.getByAddress(ipBytes);

            // Deserialize the port (4 bytes)
            int port = dataStream.readInt();

            // Return a new User object with the deserialized data
            return new User(nick, ip, port);
        }
    }
}


