package common.response;

public enum PacketData {
    ID,
    TYPE,
    DATA;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
