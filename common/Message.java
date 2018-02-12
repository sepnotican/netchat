package common;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "chatMessage")
@XmlType(propOrder = {"messageType", "timestamp", "nickname", "text"})
public class Message {
    private long timestamp;
    private String nickname;
    private String text;
    private MessageType messageType;

    public Message() {
    }

    public Message(MessageType messageType, long timestamp, String nickname, String text) {
        this.messageType = messageType;
        this.timestamp = timestamp;
        this.nickname = nickname;
        this.text = text;
    }

    @XmlAttribute(name = "timestamp")
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @XmlAttribute(name = "nickname")
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @XmlElement(name = "text")
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @XmlAttribute(name = "type")
    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    @Override
    public String toString() {
        return "Message{" +
                "timestamp=" + timestamp +
                ", nickname='" + nickname + '\'' +
                ", text='" + text + '\'' +
                ", messageType=" + messageType +
                '}';
    }
}
