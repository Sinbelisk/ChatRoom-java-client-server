package common;

import common.response.PacketData;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PacketInterpreter {
    private static final String ID = PacketData.ID.toString();
    private static final String DATA = PacketData.DATA.toString();
    private static final String TYPE = PacketData.TYPE.toString();

    private static final Logger logger = SimpleLogger.getInstance().getLogger(PacketInterpreter.class);

    /**
     * Returns a map with the values of the message;
     * @param packet
     * @return
     */
    public Map<String , String> parsePacket(String packet) {
        if (packet == null || !packet.contains(";")) {
            logger.log(Level.WARNING, "Invalid packet");
            return null; // Formato inválido
        }

        String[] parts = packet.split(";", 3);
        if (parts.length < 3) {
            logger.log(Level.WARNING, "Invalid Format");
            return null; // Formato inválido
        }

        Map<String, String> fields = new HashMap<>();
        fields.put(ID, parts[0]);
        fields.put(TYPE, parts[1]);
        fields.put(DATA, parts[2]);

        logger.log(Level.INFO, "New packet parsed: [id={0},type={1},data={3}]", new Object[]{fields.get(ID), fields.get(TYPE), fields.get(DATA)});
        return fields;
    }
}
