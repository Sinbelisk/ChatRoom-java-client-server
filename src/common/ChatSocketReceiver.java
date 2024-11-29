package Common;

import java.net.InetAddress;

public class ChatSocketReceiver extends AbstractChatSocket implements MessageReceiver{
    public ChatSocketReceiver(InetAddress ip, int port) {
        super(ip, port);
        logger = SimpleLogger.getLogger(getClass());
    }

    @Override
    public void receive() {

    }

}
