package com.example.projecttcp.protocol;

import java.io.Serializable;

public class Chat implements Serializable {
    String username;
    String message;

    public Chat(String username, String message) {
        this.username = username;
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }
}
