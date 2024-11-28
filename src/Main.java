import Common.ChatSocketReceiver;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Main {
    public static void main(String[] args) throws UnknownHostException, SocketException {
        ChatSocketReceiver receiver = new ChatSocketReceiver(InetAddress.getLocalHost(), 0);
        receiver.open();

    }
}
