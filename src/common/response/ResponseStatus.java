package common.response;

// Inner enums and class
public enum ResponseStatus {
    VALID,
    INVALID,
    ERROR,
    UNKNOWN_TYPE,
    INVALID_FORMAT;

    @Override
    public String toString() {
        return name();
    }
}
