package client;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
    private static final int BUFFER_SIZE = 1024;
    public static void main(String[] args) throws Exception {
        InetAddress host = InetAddress.getLocalHost();
        DatagramSocket serverSocket = new DatagramSocket(9876);

        byte[] received = new byte[BUFFER_SIZE];
        byte[] sended = new byte[BUFFER_SIZE];

        String text;

        while (true){

        }
    }
}
