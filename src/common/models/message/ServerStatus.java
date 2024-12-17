package common.models.message;

public enum ServerStatus {
    OK(0),
    ERROR(1),
    INFO(2);
    final int value;

    ServerStatus(int status){
        this.value = status;
    }

    public int getValue() {
        return value;
    }

    public static ServerStatus fromValue(int value) {
        for (ServerStatus type : ServerStatus.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
