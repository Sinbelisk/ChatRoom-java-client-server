package client;

import common.PacketInterpreter;
import common.UDPSocket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;

public class Client extends UDPSocket {
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    public Client() throws IOException {
        super(DEFAULT_BUFFER_SIZE);
        FileHandler fileHandler = new FileHandler("chat.log", true);
        logger.addHandler(fileHandler);
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.OFF);
    }

    @Override
    public void processPacket(DatagramPacket packet) {
        String msg = new String(packet.getData(), 0, packet.getLength());
        System.out.println("\r" + msg);
        System.out.print("\r> ");
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client();

        System.out.print("Nombre de Usuario: ");
        Scanner s = new Scanner(System.in);

        String user = s.nextLine();

        String formatted = String.format("command;%s;/login %s", user, user);
        client.send(formatted.getBytes(), InetAddress.getLocalHost(), 6969);

        Thread thread = new Thread(()->{
            while (true){
                client.receive();
            }
        });

        thread.start();
        while(true){
            System.out.print("\r> ");
            String msg = s.nextLine();

            String test = String.format("msg;%s;%s", user, msg);
            client.send(test.getBytes(), InetAddress.getLocalHost(), 6969);
        }
    }
}
