package common.models;

public enum MessageType {
    NONE,
    MSG,
    COMMAND;
    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
