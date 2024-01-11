package com.example.projecttcp.protocol;

public class ChatMessage implements Message {
    String username;
    String message;
    private byte[] image;

    public ChatMessage(String message, byte[] image) {
        this(null, message, image);
    }

    public ChatMessage(String username, String message, byte[] image) {
        this.username = username;
        this.message = message;
        this.image = image;
    }

    public ChatMessage(Chat chat) {
        this(chat.getUsername(), chat.getMessage(), chat.getImage());
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

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
