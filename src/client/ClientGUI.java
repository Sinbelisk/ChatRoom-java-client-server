package client;

import common.models.message.ClientMessage;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;

public class ClientGUI {
    private static final Scanner s = new Scanner(System.in);
    private static Client client = null;

    public static void main(String[] args) {
        // Validación de parámetros y configuración
        String ip = getIP(args);
        int port = getPort(args);

        // Intentar conectar al servidor
        try {
            client = new Client(InetAddress.getByName(ip), port);
        } catch (IOException e) {
            throw new RuntimeException("Error connecting to server: " + e.getMessage(), e);
        }

        // Mostrar IP y puerto conectados
        System.out.println("Connecting to server at " + ip + ":" + port);

        // Conectar al servidor y recibir mensajes
        connectToServer();
        startReceivingThread();

        // Manejar la entrada del usuario
        while (client.isConnected()) {
            handleUserInput();
        }
    }

    // Obtener la IP desde los parámetros
    private static String getIP(String[] args) {
        if (args.length < 2) {
            showUsageAndExit();
        }

        String ip = args[0];
        // Validar que la IP sea válida
        try {
            InetAddress.getByName(ip); // Verifica que la IP sea válida
        } catch (IOException e) {
            System.out.println("Invalid IP address: " + ip);
            System.exit(1);
        }
        return ip;
    }

    // Obtener el puerto desde los parámetros
    private static int getPort(String[] args) {
        int port;
        try {
            port = Integer.parseInt(args[1]);
            if (port < 1 || port > 65535) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid port number. Please provide a valid port (1-65535).");
            System.exit(1); // Salir si el puerto no es válido
            return -1; // Esto nunca será alcanzado, pero el compilador lo requiere
        }
        return port;
    }

    // Muestra el uso correcto de los parámetros y termina la ejecución
    private static void showUsageAndExit() {
        System.out.println("Usage: java ClientGUI <IP> <Port>");
        System.exit(1);
    }

    // Conectar al servidor
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

    // Iniciar hilo para recibir mensajes
    private static void startReceivingThread() {
        Thread thread = new Thread(() -> {
            while (client.isConnected()) {
                client.receive();
                if (client.hasMessages()) {
                    String lastMessage = client.retrieveNextMessage();
                    displayMessages(lastMessage);
                    System.out.print("\r> ");  // Mantener el prompt en la misma línea
                }
            }
        });
        thread.start();
    }

    // Manejar la entrada del usuario
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

    // Enviar mensaje al servidor
    private static void sendMessage(String msg) {
        int type = 0;
        if (msg.startsWith("/")) {
            type = 1; // Comando
            msg = msg.substring(1);
        }

        ClientMessage message = new ClientMessage(msg, client.getNick(), type);
        client.sendMessage(message);
    }

    // Desconectar y salir
    private static void disconnectAndExit() {
        System.out.println("Disconnecting...");
        client.disconnect();
        System.exit(0);
    }

    // Mostrar mensajes recibidos
    private static void displayMessages(String lastMessage) {
        System.out.println(lastMessage);
    }
}
