package org.qbychat.backend.ws.entity;

import lombok.Data;


@Data
public class ChatMessage {
    String to;
    MessageType type = MessageType.TEXT;
    String content;
    long timestamp;

    enum MessageType {
        TEXT,
        IMAGE
    }
}
