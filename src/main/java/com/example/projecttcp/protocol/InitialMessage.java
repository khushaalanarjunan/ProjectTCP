package com.example.projecttcp.protocol;

import java.util.List;

public class InitialMessage implements Message{
    List<Chat> messages;

    public InitialMessage(List<Chat> messages) {
        this.messages = messages;
    }

    @Override
    public MessageType getType() {
        return MessageType.INITIAL;
    }

    
}