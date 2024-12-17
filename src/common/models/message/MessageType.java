package common.models.message;

public enum MessageType {
    NONE(-1),
    MSG(1),
    COMMAND(2),
    INFO(3),
    CONNECTION_REQUEST(-2);

    private final int value;

    MessageType(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static MessageType fromValue(int value) {
        for (MessageType type : MessageType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
