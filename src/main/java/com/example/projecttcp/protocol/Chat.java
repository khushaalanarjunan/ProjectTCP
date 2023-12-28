package com.example.projecttcp.protocol;

public class Chat {
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
