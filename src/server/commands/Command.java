package server.commands;

public interface Command {
    void setParameters(CommandParameters parameters);
    void execute();
}
