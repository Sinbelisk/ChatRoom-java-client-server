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
    private static final Logger logger = SimpleLogger.getLogger(AbstractChatSocket.class);

    public AbstractChatSocket(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;

        logger.log(Level.INFO, "New ChatSocket created: {0}", getClass().getName());
    }

    public void open() throws SocketException {
        if (socket == null || socket.isClosed()) {
            socket = new DatagramSocket(port, ip);
            logger.log(Level.INFO, "ChatSocket {0} connected to: {1}:{2}",
                    new Object[]{getClassName(), ip.getHostName(), port});

        } else {
            logger.log(Level.WARNING, "ChatSocket {0} tried to connect, but the socket is already open!", getClassName());
        }
    }

    @Override
    public void close() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
            logger.log(Level.INFO, "ChatSocket {0} disconnected from: {1}:{2}",
                    new Object[]{getClassName(), ip.getHostName(), port});
        } else {
            logger.log(Level.WARNING, "ChatSocket {0} tried to disconnect, but the socket is already closed!", getClassName());
        }
    }

    private String getClassName() {
        return getClass().getName();
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
}
