package server.commands;

import java.util.HashMap;
import java.util.Map;

public class CommandParameters {
    private Map<String, Object> parameters = new HashMap<>();
    public CommandParameters(){

    }
    public CommandParameters addParameter(String key, Object value){
        parameters.put(key, value);
        return this;
    }
    public Object getParameter(String key){
        return parameters.get(key);
    }
}
