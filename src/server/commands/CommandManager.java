package server.commands;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private final Map<String, Command> commands = new HashMap<>();

    public CommandManager(){

    }

    public void register(String identifier, Command command){
        commands.put(identifier, command);
    }
    public void executeCommand(String identifier){
        Command command = commands.get(identifier);
        command.execute();
    }

    public Command getCommand(String identifier){
        return commands.get(identifier);
    }

}
