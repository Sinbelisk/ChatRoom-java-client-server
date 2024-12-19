package common.models;

import common.models.message.ChatMessage;

import java.util.*;

public class ChatRoom {
    private static final long INACTIVITY_THRESHOLD = 20000; // 20s
    private static final int MAX_HISTORY = 10;
    private final Set<User> users = new HashSet<>();
    private final Deque<ChatMessage> chatMessageHistory = new ArrayDeque<>(MAX_HISTORY);
    private final Map<String, Long> lastActivityTimestamps = new HashMap<>();

    public boolean addUser(User user) {
        if (users.contains(user)) return false;
        users.add(user);
        lastActivityTimestamps.put(user.getKey(), System.currentTimeMillis());
        return true;
    }

    public void removeUser(User user) {
        users.remove(user);
        lastActivityTimestamps.remove(user.getKey());
    }

    public boolean hasUser(User user) {
        return users.contains(user);
    }

    public void saveMessage(ChatMessage chatMessage) {
        if (chatMessageHistory.size() == MAX_HISTORY) chatMessageHistory.pollFirst();
        chatMessageHistory.addLast(chatMessage);
    }

    public Set<User> getUsers() {
        return users;
    }

    public String getMessageHistory() {
        StringBuilder sb = new StringBuilder();
        for (ChatMessage chatMessage : chatMessageHistory) {
            sb.append(chatMessage.getFormattedContent()).append("\n");
        }
        if (sb.isEmpty()) sb.append("Message history is empty");
        return sb.toString();
    }

    public User getUserByNick(String nick) {
        for (User user : users) {
            if (user.getNick().equalsIgnoreCase(nick)) return user;
        }
        return null;
    }

    public String listUsers() {
        StringBuilder sb = new StringBuilder();
        sb.append("Users in the room: ").append(users.size()).append("\n");
        sb.append("Full list: ").append("\n");
        for (User user : users) {
            sb.append(user.getNick()).append("\n");
        }
        return sb.replace(sb.length() - 1, sb.length(), "").toString();
    }

    // Registro de actividad
    public void updateActivity(User user) {
        lastActivityTimestamps.put(user.getKey(), System.currentTimeMillis());
    }

    /**
     * Returns the users within the specified inactive time interval
     * @return set with the inactive users
     */
    public Set<User> getInactiveUsers() {
        Set<User> inactiveUsers = new HashSet<>();
        long currentTime = System.currentTimeMillis();

        for (User user : users) {
            long lastActivity = lastActivityTimestamps.getOrDefault(user.getKey(), 0L);
            if ((currentTime - lastActivity) >= INACTIVITY_THRESHOLD) {
                inactiveUsers.add(user);
            }
        }

        return inactiveUsers;
    }

    public boolean hasInactiveUsers() {
        long currentTime = System.currentTimeMillis();

        // Iterate through users and check if any are inactive
        for (User user : users) {
            long lastActivity = lastActivityTimestamps.getOrDefault(user.getKey(), 0L);
            if ((currentTime - lastActivity) >= INACTIVITY_THRESHOLD) {
                return true;  // If we find an inactive user, return true immediately
            }
        }

        return false;  // If no inactive users were found
    }
}