package common.models;

import common.models.message.Message;

import java.util.*;

public class ChatRoom {
    private static final int MAX_HISTORY = 10;
    private final Set<User> users = new HashSet<>();
    private final Deque<Message> messageHistory = new ArrayDeque<>(MAX_HISTORY);
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
    public void saveMessage(Message message){
        if(messageHistory.size() == MAX_HISTORY) messageHistory.pollFirst();

        messageHistory.addLast(message);
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
        for (Message message : messageHistory) {
            sb.append(message.getFormattedContent()).append("\n");
        }
        return sb.toString();
    }
}
