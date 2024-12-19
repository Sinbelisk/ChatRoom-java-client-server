package server.model;

import java.net.InetAddress;
import java.util.Objects;
/**
 * The User class represents a user in the chat room. It stores the user's nickname, IP address, and port number.
 * It also provides methods for user comparison, generating a unique identifier, and retrieving user details.
 */
public class User {
    private final String nick;  // User's nickname
    private final InetAddress ip;  // User's IP address
    private final int port;  // User's port number

    /**
     * Constructor for creating a new User instance.
     *
     * @param nick The nickname of the user.
     * @param ip The IP address of the user.
     * @param port The port number the user is connected to.
     */
    public User(String nick, InetAddress ip, int port) {
        this.nick = nick;
        this.ip = ip;
        this.port = port;
    }

    /**
     * Retrieves the IP address of the user.
     *
     * @return The IP address of the user.
     */
    public InetAddress getIp() {
        return ip;
    }

    /**
     * Retrieves the port number of the user.
     *
     * @return The port number of the user.
     */
    public int getPort() {
        return port;
    }

    /**
     * Retrieves the nickname of the user.
     *
     * @return The nickname of the user.
     */
    public String getNick() {
        return nick;
    }

    /**
     * Compares this user to another object for equality. Two users are considered equal if they have the same nickname.
     *
     * @param o The object to compare this user to.
     * @return true if the users are considered equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(nick, user.nick);
    }

    /**
     * Computes the hash code of this user. The hash code is based on the user's nickname.
     *
     * @return The hash code of the user.
     */
    @Override
    public int hashCode() {
        return Objects.hash(nick);
    }

    /**
     * Creates a unique identifier for the user in the format: nick@ip:port.
     * This identifier is used for distinguishing users in the chat room.
     *
     * @return The unique key identifier of the user.
     */
    public String getKey() {
        return nick + "@" + ip.getHostAddress() + ":" + port;
    }
}

