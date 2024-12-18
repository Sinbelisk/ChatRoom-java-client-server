package common.models;

import common.models.message.ChatMessage;

public interface ChatObserver {
    void broadcastMessage(ChatMessage chatMessage);
}
