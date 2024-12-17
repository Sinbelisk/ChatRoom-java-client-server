package common;

import common.models.message.ClientMessage;
import common.models.message.MessageType;
import common.models.User;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PacketInterpreter {
    private static final Logger logger = SimpleLogger.getInstance().getLogger(PacketInterpreter.class);

    // Parses a packet data int a packet.
    public ClientMessage parsePacket(DatagramPacket rawPacket) {
        String packetContent = new String(rawPacket.getData(), 0 , rawPacket.getLength());
        InetAddress userAddress = rawPacket.getAddress();
        int userPort = rawPacket.getPort();

        String[] parts = packetContent.split(";", 3);
        if (parts.length < 3) {
            logger.log(Level.WARNING, "Invalid Format: " + Arrays.toString(parts));
            return null; // Formato inválido
        }

        MessageType type = MessageType.NONE;
        try{
            type = MessageType.valueOf(parts[0].toUpperCase());
        } catch (IllegalArgumentException e){
            logger.log(Level.WARNING, "Invalid packet type");
        }

        User owner = new User(parts[1], userAddress, userPort);
        ClientMessage msg;
        if(type.equals(MessageType.MSG)) msg = new ClientMessage(parts[2], owner, MessageType.MSG);
        else msg = new ClientMessage(packetContent, owner, MessageType.COMMAND);

        logger.log(Level.INFO, "New packetContent parsed: [id={0},type={1},data={2}]", new Object[]{msg.getId(), msg.getType().toString(), msg.getContent()});

        return msg;
    }
}
