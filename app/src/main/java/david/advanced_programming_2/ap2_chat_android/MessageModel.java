package david.advanced_programming_2.ap2_chat_android;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(indices = {@Index(value = {"content", "sent", "from", "to"}, unique = true)})
public class MessageModel implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String content;
    private String created;
    private boolean sent;
    private String from;
    private String to;

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

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
