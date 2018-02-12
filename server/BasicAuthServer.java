package server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BasicAuthServer implements AuthServer {

    private final Map<String, ClientHandler> handlerByNick = Collections.synchronizedMap(new HashMap<>());
    private final ArrayList<DummyEntry> dummyEntries = new ArrayList<>();

    BasicAuthServer() {
        dummyEntries.add(new DummyEntry("Semen", "u1", "p1"));
        dummyEntries.add(new DummyEntry("Ivan", "u2", "p2"));
        dummyEntries.add(new DummyEntry("Pupkin", "u3", "p3"));
    }


    @Override
    public ClientHandler getClientHandlerByNickname(String nickname) {
        return handlerByNick.get(nickname.toLowerCase());
    }

    @Override
    synchronized public void setClientHandlerByNickname(String nickname, ClientHandler clientHandler) {
        handlerByNick.put(nickname.toLowerCase(), clientHandler);
    }

    @Override
    public String getNickByLoginPass(String login, String password) {
        DummyEntry entry = dummyEntries.stream().filter(o ->
                o.getLogin().equals(login) && o.getPass().equals(password)
        ).findAny().orElse(null);

        if (entry == null)
            return null;
        else
            return entry.getNick();
    }


    private class DummyEntry {
        private String nick;
        private String login;
        private String pass;

        private DummyEntry(String nick, String login, String pass) {
            this.nick = nick;
            this.login = login;
            this.pass = pass;
        }

        String getNick() {
            return nick;
        }

        String getLogin() {
            return login;
        }

        String getPass() {
            return pass;
        }

    }
}
