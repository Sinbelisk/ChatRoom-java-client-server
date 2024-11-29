package common.user;

import java.net.InetAddress;
import java.util.Objects;

public class User {
    private final String nick;
    private final InetAddress address;

    public User(String nick, InetAddress address) {
        this.nick = nick;
        this.address = address;
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
}
