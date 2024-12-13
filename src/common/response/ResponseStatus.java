package common.response;

// Inner enums and class
public enum ResponseStatus {
    VALID,
    INVALID,
    INVALID_CREDENTIALS,
    ERROR,
    UNKNOWN_TYPE,
    INVALID_FORMAT,
    PROCESSED;

    @Override
    public String toString() {
        return name();
    }
}
