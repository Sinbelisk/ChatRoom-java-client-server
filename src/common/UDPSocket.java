package common;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class UDPSocket implements UDPOperation {
    private final byte[] buffer;
    private final Logger logger = SimpleLogger.getInstance().getLogger(getClass());

    protected final DatagramSocket socket;

    public UDPSocket(int port, int bufferSize) throws SocketException {
        socket = new DatagramSocket(port);
        buffer = new byte[bufferSize];

        log(Level.INFO, "Socket created on port %d with buffer size %d", port, bufferSize);
    }

    public UDPSocket(int bufferSize) throws SocketException {
        socket = new DatagramSocket();
        buffer = new byte[bufferSize];
        log(Level.INFO, "Socket created with buffer size %d", bufferSize);
    }

    @Override
    public void send(byte[] data, InetAddress address, int port) {
        try {
            DatagramPacket packet = new DatagramPacket(data, 0, data.length, address, port);
            socket.send(packet);
            log(Level.INFO, "Data sent to %s:%d, size: %d bytes", address.toString(), port, data.length);
        } catch (SocketException e) {
            log(Level.SEVERE, "Socket exception occurred while sending data: %s", e.getMessage());
        } catch (IOException e) {
            log(Level.SEVERE, "IO exception occurred while sending data: %s", e.getMessage());
        }
    }

    @Override
    public void receive() {
        try {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            log(Level.INFO, "Packet received from %s:%d, size: %d bytes",
                    packet.getAddress().toString(), packet.getPort(), packet.getLength());
            processPacket(packet);
        } catch (SocketException e) {
            log(Level.SEVERE, "Socket exception occurred while receiving data: %s", e.getMessage());
        } catch (IOException e) {
            log(Level.SEVERE, "IO exception occurred while receiving data: %s", e.getMessage());
        }
    }

    protected void log(Level level, String msg, Object... args) {
        logger.log(level, String.format(msg, args));
    }
}

