package Common;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class ChatSocketReceiver extends AbstractChatSocket implements MessageReceiver{
    public ChatSocketReceiver(InetAddress ip, int port) {
        super(ip, port);
    }

    @Override
    public void receive() {

    }

}
