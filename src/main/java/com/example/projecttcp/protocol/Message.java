package com.example.projecttcp.protocol;

import java.io.Serializable;

public interface Message extends Serializable {
    MessageType getType();
}
