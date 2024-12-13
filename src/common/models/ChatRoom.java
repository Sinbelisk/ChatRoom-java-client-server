package common.models;

import java.util.*;

public class ChatRoom {
    private static final int MAX_HISTORY = 10;
    private final Set<User> users = new HashSet<>();
    private final List<Message> messageHistory = new LinkedList<>();

    public boolean addUser(User user){
        if(users.contains(user)) return false;

        users.add(user);
        return true;
    }
    public void removeUser(User user){
        users.remove(user);
    }
    public int getCapacity(){
        return users.size();
    }

    public boolean isFull(){
        return users.size() == 10;
    }
}
