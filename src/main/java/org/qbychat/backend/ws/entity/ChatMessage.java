package org.qbychat.backend.ws.entity;

import lombok.Data;


@Data
public class ChatMessage {
    String to;
    String content;
    long timestamp;
}
