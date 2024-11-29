package common;

import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class UDPSocket {
    protected final ChatSocketConnection socket;
    protected final Logger logger;

    public UDPSocket(InetAddress ip, int port){
        this.socket = new ChatSocketConnection(ip, port);
        // This will create a logger for the client and server.
        // There's no need on make it static because there will be only one instance of both classes.
        this.logger = SimpleLogger.getLogger(getClass());
    }

    public abstract void receive();
    public abstract void send(byte[] data);

    public ChatSocketConnection getSocket() {
        return socket;
    }

    protected void log(Level level, String msg, Object... args) {
        logger.log(level, String.format(msg, args));
    }
}
