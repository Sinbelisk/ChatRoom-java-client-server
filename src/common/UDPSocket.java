package common;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class UDPSocket {
    protected final ChatSocketConnection socket;
    protected final Logger logger;

    public UDPSocket(InetAddress ip, int port) throws SocketException {
        this.socket = new ChatSocketConnection(ip, port);

        // This will create a logger for the client and server.
        // There's no need on make it static because there will be only one instance of both classes.
        this.logger = SimpleLogger.getLogger(getClass());
    }

    public UDPSocket() throws SocketException {
        this.logger = SimpleLogger.getLogger(getClass());
        this.socket = new ChatSocketConnection();
    }

    public abstract void receive() throws IOException;
    public abstract void send(byte[] data) throws IOException;

    public DatagramSocket getSocket() {
        return socket.getSocket();
    }

    protected void log(Level level, String msg, Object... args) {
        logger.log(level, String.format(msg, args));
    }
}
