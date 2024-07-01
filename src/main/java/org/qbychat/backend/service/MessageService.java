package org.qbychat.backend.service;

import org.qbychat.backend.entity.ChatMessage;

import java.util.List;

public interface MessageService {
    void addMessage(ChatMessage message);
    void removeMessage(ChatMessage message);

    List<ChatMessage> getMessages(Integer channel, boolean isGroup);
}
