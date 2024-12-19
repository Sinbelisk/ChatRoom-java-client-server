package client;

import client.model.message.ClientMessage;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;

/**
 * The main class for the client application. It connects to a server via IP and port,
 * handles user input for sending messages, and starts a thread to receive messages from the server.
 */
public class ClientMain {

    private static final Scanner s = new Scanner(System.in);
    private static Client client = null;

    /**
     * The entry point of the client application. Validates the IP and port from the command line arguments,
     * connects to the server, and manages user input and message display.
     *
     * @param args Command line arguments. Expects two arguments: the IP address and the port number.
     */
    public static void main(String[] args) {
        // Validate and retrieve the IP and port
        String ip = getIP(args);
        int port = getPort(args);

        // Attempt to connect to the server
        try {
            client = new Client(InetAddress.getByName(ip), port);
        } catch (IOException e) {
            throw new RuntimeException("Error connecting to server: " + e.getMessage(), e);
        }

        // Display the connected IP and port
        System.out.println("Connecting to server at " + ip + ":" + port);

        // Connect to the server and start receiving messages
        connectToServer();
        startReceivingThread();

        // Handle user input
        while (client.isConnected()) {
            handleUserInput();
        }
    }

    /**
     * Retrieves and validates the IP address from the command line arguments.
     *
     * @param args The command line arguments.
     * @return The validated IP address as a string.
     * @throws RuntimeException If the IP address is invalid.
     */
    private static String getIP(String[] args) {
        if (args.length < 2) {
            showUsageAndExit();
        }

        String ip = args[0];
        // Validate that the IP address is valid
        try {
            InetAddress.getByName(ip); // Verifies that the IP is valid
        } catch (IOException e) {
            System.out.println("Invalid IP address: " + ip);
            System.exit(1);
        }
        return ip;
    }

    /**
     * Retrieves and validates the port number from the command line arguments.
     *
     * @param args The command line arguments.
     * @return The validated port number.
     * @throws RuntimeException If the port number is invalid.
     */
    private static int getPort(String[] args) {
        int port;
        try {
            port = Integer.parseInt(args[1]);
            if (port < 1 || port > 65535) {
                throw new NumberFormatException("Port out of bounds");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid port number. Please provide a valid port (1-65535).");
            System.exit(1); // Exit if the port is invalid
            return -1; // This will never be reached, but required for compilation
        }
        return port;
    }

    /**
     * Prints the correct usage of command line arguments and exits the program.
     */
    private static void showUsageAndExit() {
        System.out.println("Usage: java ClientGUI <IP> <Port>");
        System.exit(1);
    }

    /**
     * Connects to the server by prompting the user to enter a username.
     * If the connection fails, it retries until successful or the user exits.
     */
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

    /**
     * Starts a new thread to receive messages from the server and display them.
     * This thread continues to run while the client is connected.
     */
    private static void startReceivingThread() {
        Thread thread = new Thread(() -> {
            while (client.isConnected()) {
                client.receive();
                if (client.hasMessages()) {
                    String lastMessage = client.retrieveNextMessage();
                    displayMessages(lastMessage);
                    System.out.print("\r> ");  // Keep the prompt on the same line
                }
            }
        });
        thread.start();
    }

    /**
     * Handles user input by prompting for a message. If the message is not empty or an exit command,
     * it sends the message to the server.
     */
    private static void handleUserInput() {
        System.out.print("> ");
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

    /**
     * Sends a message to the server. If the message starts with '/', it is considered a command.
     *
     * @param msg The message to send.
     */
    private static void sendMessage(String msg) {
        int type = 0;
        if (msg.startsWith("/")) {
            type = ClientMessage.COMMAND;
            msg = msg.substring(1);
        }

        ClientMessage message = new ClientMessage(msg, client.getNick(), type);
        client.sendMessage(message);
    }

    /**
     * Disconnects from the server and exits the program.
     */
    private static void disconnectAndExit() {
        System.out.println("Disconnecting...");
        client.disconnect();
        System.exit(0);
    }

    /**
     * Displays the last received message from the server.
     *
     * @param lastMessage The message to display.
     */
    private static void displayMessages(String lastMessage) {
        System.out.println(lastMessage);
    }
}
