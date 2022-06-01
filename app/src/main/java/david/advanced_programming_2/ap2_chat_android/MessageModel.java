package david.advanced_programming_2.ap2_chat_android;

import java.io.Serializable;

public class MessageModel implements Serializable {
    private final String content;
    private final String created;
    private final boolean sent;
    private final String from;
    private final String to;

    public MessageModel(String content, String created, boolean sent, String from, String to) {
        this.content = content;
        this.created = created;
        this.sent = sent;
        this.from = from;
        this.to = to;
    }

    public String getContent() {
        return content;
    }

    public String getCreated() {
        return created;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }
    public boolean isSent() {
        return sent;
    }
}
