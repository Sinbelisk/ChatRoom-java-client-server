package common.models;

import common.models.message.ClientMessage;

public interface ChatObserver {
    void broadcastMessage(ClientMessage clientMessage);
}
