package common.models;

import common.models.message.Message;

public interface ChatObserver {
    void broadcastMessage(Message message);
}
