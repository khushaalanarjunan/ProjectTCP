package com.example.projecttcp.protocol;

public class ChatMessage implements Message {
    String username;
    String message;
    
    public ChatMessage(String message) {
        this(null, message);
    }

    public ChatMessage(String username, String message) {
        this.username = username;
        this.message = message;
    }

    public ChatMessage(Chat chat) {
        this(chat.getUsername(), chat.getMessage());
    }

    @Override
    public MessageType getType() {
       return MessageType.CHAT;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }
    
}
