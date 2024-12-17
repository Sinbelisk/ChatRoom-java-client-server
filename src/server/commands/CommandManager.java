package server.commands;

import common.models.ChatRoom;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private final Map<String, Command> commands = new HashMap<>();
    private final ChatRoom chatRoom;
    public CommandManager(ChatRoom chatRoom){
        this.chatRoom = new ChatRoom();

        commands.put("login", new LoginCommand(chatRoom));
    }
}
