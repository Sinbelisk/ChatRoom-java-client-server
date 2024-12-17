package common.models.message;

public enum MessageType {
    NONE,
    MSG,
    COMMAND,
    INFO;
    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
