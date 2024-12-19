package common;

import common.util.SimpleLogger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The UDPSocket class provides a basic implementation of the UDPOperation interface for handling UDP communications.
 * It provides the functionality to send and receive UDP packets, as well as logging the actions performed.
 */
public abstract class UDPSocket implements UDPOperation {
    protected final byte[] buffer;  // Buffer used for receiving data
    protected final Logger logger = SimpleLogger.getInstance().getLogger(getClass());  // Logger for logging messages
    protected final DatagramSocket socket;  // The underlying DatagramSocket for UDP communication

    /**
     * Constructs a new UDPSocket instance with the specified port and buffer size.
     *
     * @param port The port number to bind the socket to.
     * @param bufferSize The size of the buffer to use for receiving data.
     * @throws SocketException If there is an error creating or binding the socket.
     */
    public UDPSocket(int port, int bufferSize) throws SocketException {
        socket = new DatagramSocket(port);  // Create socket bound to the specified port
        buffer = new byte[bufferSize];  // Set up the buffer for receiving data

        log(Level.INFO, "Socket created on port %d with buffer size %d", port, bufferSize);
    }

    /**
     * Constructs a new UDPSocket instance with a default port and specified buffer size.
     *
     * @param bufferSize The size of the buffer to use for receiving data.
     * @throws SocketException If there is an error creating the socket.
     */
    public UDPSocket(int bufferSize) throws SocketException {
        socket = new DatagramSocket();  // Create an unbound socket
        buffer = new byte[bufferSize];  // Set up the buffer for receiving data
        log(Level.INFO, "Socket created with buffer size %d", bufferSize);
    }

    /**
     * Sends the specified data to the given address and port via UDP.
     *
     * @param data The data to send as a byte array.
     * @param address The destination address to send the data to.
     * @param port The destination port to send the data to.
     */
    @Override
    public void send(byte[] data, InetAddress address, int port) {
        try {
            DatagramPacket packet = new DatagramPacket(data, 0, data.length, address, port);
            socket.send(packet);  // Send the packet over UDP
            log(Level.INFO, "Data sent to %s:%d, size: %d bytes", address.toString(), port, data.length);
        } catch (SocketException e) {
            log(Level.SEVERE, "Socket exception occurred while sending data: %s", e.getMessage());
        } catch (IOException e) {
            log(Level.SEVERE, "IO exception occurred while sending data: %s", e.getMessage());
        }
    }

    /**
     * Receives a DatagramPacket from the socket. This method blocks until a packet is received.
     */
    @Override
    public void receive() {
        try {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);  // Receive a packet from the socket
            log(Level.INFO, "Packet received from %s:%d, size: %d bytes",
                    packet.getAddress().toString(), packet.getPort(), packet.getLength());
            processPacket(packet);  // Process the received packet
        } catch (SocketException e) {
            log(Level.SEVERE, "Socket exception occurred while receiving data: %s", e.getMessage());
        } catch (IOException e) {
            log(Level.SEVERE, "IO exception occurred while receiving data: %s", e.getMessage());
        }
    }

    /**
     * Logs messages at the specified level with the provided message format and arguments.
     *
     * @param level The logging level (e.g., INFO, SEVERE).
     * @param msg The format string for the log message.
     * @param args The arguments to format the message string.
     */
    public void log(Level level, String msg, Object... args) {
        logger.log(level, String.format(msg, args));
    }
}

