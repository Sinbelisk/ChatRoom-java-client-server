package server.model.message;

import server.model.User;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * The ChatMessage class represents a message exchanged between users in the chat room.
 * It contains the content of the message, the owner (user who sent the message),
 * the timestamp of when the message was created, and a unique identifier for the message.
 */
public class ChatMessage {
    private final String id;  // Unique identifier for the message
    private final String content;  // The content of the message
    private final User owner;  // The user who sent the message
    private final Timestamp time;  // The timestamp of when the message was created

    /**
     * Constructor for creating a new ChatMessage instance.
     * The message is assigned a unique identifier, and the timestamp is set to the current time.
     *
     * @param content The content of the message.
     * @param owner The user who is sending the message.
     */
    public ChatMessage(String content, User owner) {
        this.id = UUID.randomUUID().toString();  // Generate a unique ID for the message
        this.content = content;
        this.owner = owner;
        this.time = Timestamp.from(Instant.now());  // Set the time to the current moment
    }

    /**
     * Retrieves the content of the chat message.
     *
     * @return The content of the chat message.
     */
    public String getContent() {
        return content;
    }

    /**
     * Retrieves the timestamp of when the message was created.
     *
     * @return The timestamp of the chat message.
     */
    public Timestamp getTime() {
        return time;
    }

    /**
     * Retrieves the user who sent the message.
     *
     * @return The user who is the owner of the message.
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Retrieves the unique identifier of the chat message.
     *
     * @return The ID of the chat message.
     */
    public String getId() {
        return id;
    }

    /**
     * Formats the message content for display in the chat room,
     * including the owner's nickname and the message content.
     *
     * @return A formatted string representation of the message.
     */
    public String getFormattedContent() {
        return String.format("<%s> %s", owner.getNick(), content);
    }

    /**
     * Formats the message content for private messaging,
     * including the owner's nickname and the message content.
     *
     * @return A formatted string representation of the private message.
     */
    public String getFormattedContentAsPrivate() {
        return String.format("<private from %s> %s", owner.getNick(), getContent());
    }

    /**
     * Checks if two chat messages are equal. Two messages are considered equal
     * if they have the same unique identifier.
     *
     * @param o The object to compare with the current chat message.
     * @return True if the messages are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessage chatMessage = (ChatMessage) o;
        return Objects.equals(id, chatMessage.id);
    }

    /**
     * Returns the hash code of the chat message, based on its unique identifier.
     *
     * @return The hash code of the chat message.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Returns a string representation of the chat message,
     * including its ID, content, owner, and timestamp.
     *
     * @return A string representation of the chat message.
     */
    @Override
    public String toString() {
        return "{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", owner=" + owner.getNick() +
                ", time=" + time +
                '}';
    }
}
