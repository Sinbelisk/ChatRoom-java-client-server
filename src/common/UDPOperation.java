package common;

import java.net.DatagramPacket;
import java.net.InetAddress;

public interface UDPOperation {
    void send(byte[] data, InetAddress address, int port);
    void receive();
    void processPacket(DatagramPacket packet);
}
