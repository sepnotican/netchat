package client;

import common.Message;
import common.MessageType;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryFormatter {

    private static final String NICKNAME_COLOR = "black";
    private static final String TIME_COLOR = "gray";
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy HH:mm:ss");
    private final Map<MessageType, String> colors = new HashMap<>();

    HistoryFormatter() {
        colors.put(MessageType.INFO, "red");
        colors.put(MessageType.SERVER_BROADCAST, "blue");
        colors.put(MessageType.BROADCAST, "black");
        colors.put(MessageType.WHISPER, "purple");
    }

    public String getFormattedHistory(List<Message> history) {
        final StringBuilder result = new StringBuilder();

        history.forEach(msg -> {
            String nick;
            if (msg.getMessageType() == MessageType.BROADCAST)
                nick = msg.getNickname();
            else
                nick = "";

            result
                    .append("<span class=\"TIME_COLOR\">").append(sdf.format(msg.getTimestamp())).append("</span> > ")
                    .append("<b class=\"NICKNAME_COLOR\">")
                    .append(nick).append(':').append("</b>")
                    .append(String.format("<span class=\"%s\">", msg.getMessageType().name()))
                    .append(msg.getText()).append("</span>")
                    .append("<br/>");
        });

        return wrapHTMLStyle(result.toString());
    }

    private String wrapHTMLStyle(String historyText) {
        return "<!DOCTYPE html>" +
                "<HTML><style> body{font-size:9pt; font-family:monospace}" +
                "\n.NICKNAME_COLOR{color:" + NICKNAME_COLOR + ";}" +
                "\n.WHISPER{color : " + colors.get(MessageType.WHISPER) + ";}" +
                "\n.BROADCAST{color : " + colors.get(MessageType.BROADCAST) + ";}" +
                "\n.SERVER_BROADCAST{color : " + colors.get(MessageType.SERVER_BROADCAST) + ";}" +
                "\n.INFO{color : " + colors.get(MessageType.INFO) + ";}" +
                "\n.TIME_COLOR{color : " + TIME_COLOR + ";}" +
                "</style><BODY>" +
                historyText
                + "</BODY></HTML>";
    }

}
