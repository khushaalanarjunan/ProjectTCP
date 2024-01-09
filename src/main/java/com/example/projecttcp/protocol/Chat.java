package com.example.projecttcp.protocol;

import java.io.Serializable;

public class Chat implements Serializable {
    String username;
    String message;
    private byte[] image;

    public Chat(String username, String message, byte[] image) {
        this.username = username;
        this.message = message;
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    public byte[] getImage() {
        return image;
    }
}
