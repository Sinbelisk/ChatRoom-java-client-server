package common.models.message;

public class ClientMessage {
    public static final int COMMAND  = 1;
    public static final int MSG = 0;

    private final String content;
    private final String nick;
    private final int type; // 0 MSG; 1 COMMAND

    public ClientMessage(String content, String nick, int type){
        this.content = content;
        this.nick = nick;
        this.type = type;
    }

    public String getContent() {
        return content;
    }
    public String getNick() {
        return nick;
    }

    public int getType() {
        return type;
    }
}
