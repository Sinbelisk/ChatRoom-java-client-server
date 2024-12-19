package server.model;

import server.model.message.ChatMessage;

import java.util.*;

/**
 * The ChatRoom class represents a virtual room where users can interact with each other by sending messages.
 * It handles user management, message history, and checks for inactive users.
 * Users can send messages, view message history, and communicate with other users in the room.
 */
public class ChatRoom {
    private static final long INACTIVITY_THRESHOLD = 20000; // 20s: Threshold for inactivity detection
    private static final int MAX_HISTORY = 10; // Maximum number of messages to store in the history
    private final Set<User> users = new HashSet<>();  // Set of users currently in the chat room
    private final Deque<ChatMessage> chatMessageHistory = new ArrayDeque<>(MAX_HISTORY);  // History of chat messages
    private final Map<String, Long> lastActivityTimestamps = new HashMap<>();  // Timestamps of user activity

    /**
     * Adds a user to the chat room.
     * If the user is already in the room, they are not added again.
     *
     * @param user The user to add to the room.
     * @return true if the user was added successfully, false if the user was already in the room.
     */
    public boolean addUser(User user) {
        if (users.contains(user)) return false;
        users.add(user);
        lastActivityTimestamps.put(user.getKey(), System.currentTimeMillis());  // Update activity timestamp
        return true;
    }

    /**
     * Removes a user from the chat room.
     *
     * @param user The user to remove from the room.
     */
    public void removeUser(User user) {
        users.remove(user);
        lastActivityTimestamps.remove(user.getKey());
    }

    /**
     * Checks if a user is currently in the chat room.
     *
     * @param user The user to check.
     * @return true if the user is in the room, false otherwise.
     */
    public boolean hasUser(User user) {
        return users.contains(user);
    }

    /**
     * Saves a chat message in the chat room's history.
     * If the history is full, the oldest message is removed.
     *
     * @param chatMessage The message to save.
     */
    public void saveMessage(ChatMessage chatMessage) {
        if (chatMessageHistory.size() == MAX_HISTORY) chatMessageHistory.pollFirst();
        chatMessageHistory.addLast(chatMessage);
    }

    /**
     * Retrieves the set of users currently in the chat room.
     *
     * @return A set of users in the room.
     */
    public Set<User> getUsers() {
        return users;
    }

    /**
     * Retrieves the chat message history as a formatted string.
     *
     * @return A string representing the chat message history.
     */
    public String getMessageHistory() {
        StringBuilder sb = new StringBuilder();
        for (ChatMessage chatMessage : chatMessageHistory) {
            sb.append(chatMessage.getFormattedContent()).append("\n");
        }
        if (sb.isEmpty()) sb.append("Message history is empty");
        return sb.toString();
    }

    /**
     * Retrieves a user in the chat room by their nickname.
     *
     * @param nick The nickname of the user.
     * @return The user with the given nickname, or null if no such user exists.
     */
    public User getUserByNick(String nick) {
        for (User user : users) {
            if (user.getNick().equalsIgnoreCase(nick)) return user;
        }
        return null;
    }

    /**
     * Retrieves a list of users in the chat room.
     *
     * @return A formatted string listing the users in the room.
     */
    public String listUsers() {
        StringBuilder sb = new StringBuilder();
        sb.append("Users in the room: ").append(users.size()).append("\n");
        sb.append("Full list: ").append("\n");
        for (User user : users) {
            sb.append(user.getNick()).append("\n");
        }
        return sb.replace(sb.length() - 1, sb.length(), "").toString();
    }

    /**
     * Updates the last activity timestamp for a user.
     *
     * @param user The user whose activity timestamp should be updated.
     */
    public void updateActivity(User user) {
        lastActivityTimestamps.put(user.getKey(), System.currentTimeMillis());
    }

    /**
     * Returns a set of users who have been inactive for longer than the inactivity threshold.
     *
     * @return A set of users who have been inactive for longer than the threshold.
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

    /**
     * Checks if there are any inactive users in the chat room.
     *
     * @return true if there are inactive users, false otherwise.
     */
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