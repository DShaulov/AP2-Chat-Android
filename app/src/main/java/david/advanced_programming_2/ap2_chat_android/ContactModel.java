package david.advanced_programming_2.ap2_chat_android;

import java.io.Serializable;

public class ContactModel implements Serializable {
    private final String id;
    private final String name;
    private final String server;
    private final String last;
    private final String lastdate;
    private final String whose;
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

    public String getName() {
        return name;
    }

    public String getLast() {
        return last;
    }

    public String getLastDate() {
        return lastdate;
    }

    public String getServer() {
        return server;
    }

    public String getWhose() {
        return whose;
    }
}
