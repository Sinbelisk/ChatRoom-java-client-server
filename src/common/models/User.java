package common.models;

import java.net.InetAddress;
import java.util.Objects;
public class User{
    private final String nick;
    private final InetAddress ip;
    private final int port;
    public User(String nick, InetAddress ip, int port) {
        this.nick = nick;
        this.ip = ip;
        this.port = port;
    }

    public InetAddress getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getNick() {
        return nick;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(nick, user.nick);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nick);
    }
    public String getKey() {
        return nick + "@" + ip.getHostAddress() + ":" + port;
    }
}
