package common;

import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * The UDPOperation interface defines the essential operations for UDP communication.
 * Any class implementing this interface must provide methods to send data,
 * receive data, and process received packets.
 */
public interface UDPOperation {

    /**
     * Sends the given data to the specified address and port.
     *
     * @param data The data to send, in byte array form.
     * @param address The destination InetAddress to which the data will be sent.
     * @param port The destination port number.
     */
    void send(byte[] data, InetAddress address, int port);

    /**
     * Receives a DatagramPacket from the socket.
     */
    void receive();

    /**
     * Processes the received DatagramPacket.
     *
     * @param packet The DatagramPacket that has been received.
     */
    void processPacket(DatagramPacket packet);
}
