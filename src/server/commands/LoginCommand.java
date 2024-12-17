package server.commands;

import common.models.ChatRoom;
import common.models.User;

public class LoginCommand implements Command{
    private ChatRoom chatRoom;
    private CommandParameters parameters;
    public LoginCommand(ChatRoom chatRoom){
        this.chatRoom = chatRoom;
    }

    @Override
    public void setParameters(CommandParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public void execute() {
        User user = (User) parameters.getParameter("user");
        chatRoom.removeUser(user);
    }
}
