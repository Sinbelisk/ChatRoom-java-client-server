package common.models;

public interface ChatObserver {
    void broadcastMessage(Message message);
}
