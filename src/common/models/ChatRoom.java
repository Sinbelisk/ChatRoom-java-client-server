package common.models;

import common.models.message.ChatMessage;

import java.util.*;

public class ChatRoom {
    private static final int MAX_HISTORY = 10;
    private final Set<User> users = new HashSet<>();
    private final Deque<ChatMessage> chatMessageHistory = new ArrayDeque<>(MAX_HISTORY);
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
    public void saveMessage(ChatMessage chatMessage){
        if(chatMessageHistory.size() == MAX_HISTORY) chatMessageHistory.pollFirst();

        chatMessageHistory.addLast(chatMessage);
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
        for (ChatMessage chatMessage : chatMessageHistory) {
            sb.append(chatMessage.getFormattedContent()).append("\n");
        }
        if(sb.isEmpty()) sb.append("Message history is empty");
        return sb.toString();
    }

    public User getUserByNick(String nick){
        for (User user : users) {
            if(user.getNick().equalsIgnoreCase(nick)) return user;
        }
        return null;
    }

    public String listUsers(){
        StringBuilder sb = new StringBuilder();
        sb.append("Users in the room: ").append(users.size()).append("\n");
        sb.append("Full list: ").append("\n");
        for (User user : users) {
            sb.append(user.getNick()).append("\n");
        }
        return sb.replace(sb.length()-1, sb.length(), "").toString();
    }
}
