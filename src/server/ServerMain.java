package server;

import java.net.SocketException;

import java.net.SocketException;

/**
 * The main class for the server application. It validates and processes the port argument
 * from the command line and starts the server with the provided port.
 */
public class ServerMain {

    /**
     * The entry point of the server application. It validates the port from command line arguments
     * and starts the server with the validated port.
     *
     * @param args Command line arguments. Expects one argument: the port number.
     */
    public static void main(String[] args) {
        try {
            // Validate and get the port
            int port = validateAndGetPort(args);

            // Create and run the server on the specified port
            startServer(port);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        } catch (SocketException e) {
            System.err.println("Error initializing server socket: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Validates and retrieves the port from the command line arguments.
     *
     * @param args The command line arguments.
     * @return The validated port number.
     * @throws IllegalArgumentException If the port is invalid.
     */
    private static int validateAndGetPort(String[] args) {
        // Check if the port argument is valid
        if (!isValidPortArgs(args)) {
            printUsageAndExit();
        }

        // Retrieve and validate the port number
        return parsePort(args);
    }

    /**
     * Checks if the command line arguments contain a valid port argument.
     *
     * @param args The command line arguments.
     * @return True if the arguments are valid (contains one port argument), false otherwise.
     */
    private static boolean isValidPortArgs(String[] args) {
        return args.length == 1;
    }

    /**
     * Prints the usage message and exits the application.
     */
    private static void printUsageAndExit() {
        System.out.println("Usage: java ServerMain <Port>");
        System.exit(1);
    }

    /**
     * Parses the port number from the command line arguments and validates it.
     *
     * @param args The command line arguments.
     * @return The parsed port number.
     * @throws IllegalArgumentException If the port number is invalid (outside the range 1-65535).
     */
    private static int parsePort(String[] args) {
        int port;

        try {
            port = Integer.parseInt(args[0]);
            // Check if the port is in a valid range
            if (port < 1 || port > 65535) {
                throw new IllegalArgumentException("Port must be between 1 and 65535.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid port number. Please provide a valid port (1-65535).");
        }

        return port;
    }

    /**
     * Starts the server on the specified port.
     *
     * @param port The port on which the server should run.
     * @throws SocketException If there is an error initializing the server socket.
     */
    private static void startServer(int port) throws SocketException {
        try {
            new Server(port).run();
        } catch (SocketException e) {
            throw new SocketException("Failed to bind the server to port " + port + ": " + e.getMessage());
        }
    }
}
