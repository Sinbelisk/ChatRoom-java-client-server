package server.commands;

import common.models.User;
import common.models.message.ClientMessage;

public interface Command {
    void execute(String[] elements, ClientMessage message, User owner);
}
