package com.example.projecttcp.protocol;

import java.util.List;

public class InitialMessage implements Message{
    List<Chat> chats;

    public List<Chat> getChats() {
        return chats;
    }

    public InitialMessage(List<Chat> chats) {
        this.chats = chats;
    }

    @Override
    public MessageType getType() {
        return MessageType.INITIAL;
    }

    
}