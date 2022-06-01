package david.advanced_programming_2.ap2_chat_android;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class ContactModel implements Serializable {
    @PrimaryKey
    int daoId;
    private String id;
    private String name;
    private String server;
    private String last;
    private String lastdate;
    private String whose;
    public ContactModel(String id, String name, String server, String last, String lastdate, String whose) {
        this.id = id;
        this.name = name;
        this.server = server;
        this.last = last;
        this.lastdate = lastdate;
        this.whose = whose;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getLastdate() {
        return lastdate;
    }

    public void setLastdate(String lastdate) {
        this.lastdate = lastdate;
    }

    public String getWhose() {
        return whose;
    }

    public void setWhose(String whose) {
        this.whose = whose;
    }
}
