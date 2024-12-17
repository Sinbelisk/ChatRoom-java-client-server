package server;

import common.models.User;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerConstants {
    public static final int SERVER_PORT = 6969;
    public static InetAddress SERVER_ADDRESS;
    public static User SERVER_USER = new User("server", SERVER_ADDRESS, SERVER_PORT);

    static {
        try {
            SERVER_ADDRESS = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
