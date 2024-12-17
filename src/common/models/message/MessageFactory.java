package common.models.message;

import common.models.User;
import server.ServerConstants;

public class MessageFactory {
    public static Message createChatMessage(String content, User owner) {
        return new Message(content, owner, MessageType.MSG);
    }

    public static Message createStatusMessage(String content) {
        return new Message(content, ServerConstants.SERVER_USER, MessageType.INFO);
    }
}

