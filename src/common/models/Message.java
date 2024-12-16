package common.models;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Message {
    private final String id;
    private final String content;
    private final MessageType type;
    private final User owner;
    private final Timestamp time;

    public Message(String content, User owner, MessageType type) {
        this.id = UUID.randomUUID().toString();
        this.content = content;
        this.owner = owner;
        this.time = Timestamp.from(Instant.now());
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public Timestamp getTime() {
        return time;
    }

    public User getOwner() {
        return owner;
    }

    public String getId() {
        return id;
    }

    public MessageType getType() {
        return type;
    }

    public String getFormattedContent(){
        return String.format("<%s> %s", owner.getNick(), content);
    }

    public byte[] getBytes(){
        return getFormattedContent().getBytes();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(id, message.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
