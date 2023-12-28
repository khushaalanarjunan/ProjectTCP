package com.example.projecttcp.protocol;

public class RegistrationMessage implements Message {
    String username;

    public String getUsername() {
        return username;
    }

    public RegistrationMessage(String username) {
        this.username = username;
    }

    @Override
    public MessageType getType() {
        return MessageType.REGISTRATION;
    }
}
