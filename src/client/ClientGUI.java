package client;

import common.models.message.ClientMessage;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;

public class ClientGUI {
    private static final Scanner s = new Scanner(System.in);
    private static Client client = null;

    public static void main(String[] args) {

        try {
            client = new Client(InetAddress.getLocalHost(), 6969);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        connectToServer();
        startReceivingThread();

        while (client.isConnected()) {
            handleUserInput();
        }
    }

    private static void connectToServer() {
        String user;

        while (!client.isConnected()) {
            System.out.print("Insert username (or 'exit' to exit): ");
            user = s.nextLine().trim();

            if ("exit".equalsIgnoreCase(user)) {
                System.out.println("Exiting...");
                break;
            }

            client.connect(user);
            if (!client.isConnected()) {
                String cause = client.retrieveNextMessage();

                if (cause == null) cause = "Cannot establish connection with the server";
                System.out.printf("Unable to connect, cause: %s%n", cause);
            }
        }
    }

    private static void startReceivingThread() {
        Thread thread = new Thread(() -> {
            while (client.isConnected()) {
                client.receive();

                // Handle incoming messages
                if (client.hasMessages()) {
                    String lastMessage = client.retrieveNextMessage();
                    displayMessages(lastMessage);  // Display new message
                }
            }
        });
        thread.start();
    }

    private static void handleUserInput() {
        System.out.print("\r");  // Only show > when expecting user input (use \r to overwrite the line)
        String msg = s.nextLine().trim();

        if (msg.isEmpty()) {
            System.out.println("Message cannot be empty.");
            return;
        }

        if ("exit".equalsIgnoreCase(msg)) {
            disconnectAndExit();
            return;
        }

        sendMessage(msg);
    }

    private static void sendMessage(String msg) {
        int type = 0;
        if (msg.startsWith("/")) {
            type = 1; // Command
            msg = msg.substring(1);
        }

        ClientMessage message = new ClientMessage(msg, client.getNick(), type);
        client.sendMessage(message);
    }

    private static void disconnectAndExit() {
        System.out.println("Disconnecting...");
        client.disconnect();
        System.exit(0);
    }

    private static void displayMessages(String lastMessage) {
        // Display the last received message
        System.out.println(lastMessage);
    }
}

