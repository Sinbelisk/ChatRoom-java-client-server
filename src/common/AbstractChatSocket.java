package common;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

// TODO: address validation when opened with a custom address and port
// TODO: more logging.
public abstract class AbstractChatSocket implements AutoCloseable {
    protected DatagramSocket socket;
    protected InetAddress ip;
    protected int port;
    protected static Logger logger = SimpleLogger.getLogger(AbstractChatSocket.class);

    /**
     * Instantiates the socket and sets an ip and port.
     * @param ip the host ip.
     * @param port the host port.
     */
    public AbstractChatSocket(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;

        log(Level.INFO, "New Socket created: <%s>", getClassName());
    }

    /**
     * Opens the socket with a specified ip and port, it rewrites the current ip and port of
     * the class.
     * @param ip host ip
     * @param port the port
     * @throws SocketException if the connection cannot be done.
     */
    public void open(InetAddress ip, int port) throws SocketException{
        this.ip = ip;
        this.port = port;
        open();
    }

    /**
     * Opens socket using the stored ip and port.
     * @throws SocketException if the opening cannot be done.
     */
    public void open() throws SocketException {
        if (socket == null || socket.isClosed()) {
            socket = new DatagramSocket(port, ip);
            log(Level.INFO, "Socket <%s> connected to: %s:%d", getClassName(), ip.getHostAddress(), port);
        } else {
            log(Level.WARNING, "Socket <%s> tried to connect, but the socket is already open! [Status: Bound to %s:%d]",
                    getClassName(), socket.getLocalAddress(), socket.getLocalPort());
        }
    }

    /**
     * Closes the current socket.
     */
    @Override
    public void close() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
            log(Level.INFO, "Socket <%s> disconnected from: %s:%d", getClassName(), ip.getHostAddress(), port);
        } else {
            log(Level.WARNING, "Socket <%s> tried to disconnect, but the socket is already closed! [Status: Bound to %s:%d]",
                    getClassName(), socket.getLocalAddress(), socket.getLocalPort());
        }
    }

    private String getClassName() {
        return getClass().getSimpleName();
    }
    private boolean isValidAddress(InetAddress ip, int port){
        return (ip != null && port >= 0);
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
