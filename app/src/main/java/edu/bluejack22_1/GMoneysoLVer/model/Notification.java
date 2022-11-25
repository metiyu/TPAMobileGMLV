package edu.bluejack22_1.GMoneysoLVer.model;

import java.io.Serializable;

public class Notification implements Serializable {
    private String id, message;

    public Notification(String id, String message) {
        this.id = id;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
