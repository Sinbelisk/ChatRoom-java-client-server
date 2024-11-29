package Common;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractChatSocket implements AutoCloseable {
    protected DatagramSocket socket;
    protected InetAddress ip;
    protected int port;
    protected static final Logger logger = SimpleLogger.getLogger(AbstractChatSocket.class);

    public AbstractChatSocket(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;

        log(Level.INFO, "New ChatSocket created: <%s>", getClassName());
    }

    public void open() throws SocketException {
        if (socket == null || socket.isClosed()) {
            socket = new DatagramSocket(port, ip);
            log(Level.INFO, "ChatSocket <%s> connected to: %s:%d", getClassName(), ip.getHostAddress(), port);
        } else {
            log(Level.WARNING, "ChatSocket <%s> tried to connect, but the socket is already open! [Status: Bound to %s:%d]",
                    getClassName(), socket.getLocalAddress(), socket.getLocalPort());
        }
    }

    @Override
    public void close() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
            log(Level.INFO, "ChatSocket <%s> disconnected from: %s:%d", getClassName(), ip.getHostAddress(), port);
        } else {
            log(Level.WARNING, "ChatSocket <%s> tried to disconnect, but the socket is already closed! [Status: Bound to %s:%d]",
                    getClassName(), socket.getLocalAddress(), socket.getLocalPort());
        }
    }

    private String getClassName() {
        return getClass().getSimpleName();
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public InetAddress getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    /**
     * Shared logging method for all subclasses.
     * @param level severity.
     * @param msg message of the log.
     * @param args arguments from the message.
     */
    protected void log(Level level, String msg, Object... args) {
        logger.log(level, String.format(msg, args));
    }
}
