package common.response;
public enum PacketType {
    MESSAGE,
    COMMAND;

    public static PacketType fromString(String type) {
        try {
            return PacketType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // Tipo desconocido
        }
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
