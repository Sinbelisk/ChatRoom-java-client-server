package common.models;

import common.models.message.ClientMessage;

import java.util.*;

public class ChatRoom {
    private static final int MAX_HISTORY = 10;
    private final Set<User> users = new HashSet<>();
    private final Deque<ClientMessage> clientMessageHistory = new ArrayDeque<>(MAX_HISTORY);
    public boolean addUser(User user){
        if(users.contains(user)) return false;

        users.add(user);
        return true;
    }
    public void removeUser(User user){
        users.remove(user);
    }
    public boolean hasUser(User user){
        return users.contains(user);
    }
    public void saveMessage(ClientMessage clientMessage){
        if(clientMessageHistory.size() == MAX_HISTORY) clientMessageHistory.pollFirst();

        clientMessageHistory.addLast(clientMessage);
    }
    public int getCapacity(){
        return users.size();
    }

    public boolean isFull(){
        return users.size() == 10;
    }

    public Set<User> getUsers() {
        return users;
    }
    public String getMessageHistory(){
        StringBuilder sb = new StringBuilder();
        for (ClientMessage clientMessage : clientMessageHistory) {
            sb.append(clientMessage.getFormattedContent()).append("\n");
        }
        return sb.toString();
    }
}
