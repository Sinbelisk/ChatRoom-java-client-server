package common.response;

import common.SimpleLogger;

import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class ResponseFactory {
    private static final String ID = PacketData.ID.toString();
    private static final String DATA = PacketData.DATA.toString();
    private static final String TYPE = PacketData.TYPE.toString();

    private static final Logger logger = SimpleLogger.getInstance().getLogger(ResponseFactory.class);

    public Response createResponse(Map<String, String> parsedPacket) {
        if (parsedPacket == null) {
            return new Response(generateId(), ResponseStatus.INVALID_FORMAT, null);
        }

        String id = parsedPacket.get(ID);
        String typeString = parsedPacket.get(TYPE);
        String data = parsedPacket.get(DATA);

        return null;
    }

    private String generateId(){
        return UUID.randomUUID().toString();
    }
}
