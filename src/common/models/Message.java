package common.models;

import java.sql.Timestamp;

public class Message {
    private final String content;
    private final User owner;
    private final Timestamp time;

    public Message(String content, User owner, Timestamp time) {
        this.content = content;
        this.owner = owner;
        this.time = time;
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
}
