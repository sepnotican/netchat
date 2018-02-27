package client;

import common.Message;

import java.util.LinkedList;
import java.util.List;

public class ChatHistory {

    private static final int HISTORY_SIZE = 1000;
    private static ChatHistory instance;

    static {
        instance = new ChatHistory();
    }

    private LinkedList<Message> history = new LinkedList<Message>(); //todo LHM + save formatted history

    private ChatHistory() {
    }

    public static ChatHistory getInstance() {
        return instance;
    }

    public void append(Message message) {
        history.add(message);
        if (history.size() > HISTORY_SIZE)
            history.removeFirst();
    }

    public List<Message> getHistory() {
        return history;
    }
}
