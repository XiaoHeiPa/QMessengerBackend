package org.qbychat.backend.service;

import org.qbychat.backend.entity.ChatMessage;

import java.util.List;

public interface MessageService {
    void addMessage(ChatMessage message);
    void removeMessage(ChatMessage message);
    void removeMessage(int messageId);

    /**
     * 获取最近的信息 (两页)
     *
     * @param channel 频道, 通常为群组编号或者用户编号
     * */
    List<ChatMessage> fetchLatestGroupMessages(int channel);
    List<ChatMessage> fetchLatestDirectMessages(int user1, int user2);
    /**
     * 获取群组消息
     *
     * @param channel 频道, 通常为群组编号或者用户编号
     * @param page 页数 (倒序)
     * */
    List<ChatMessage> fetchGroupMessages(int channel, int page);
    List<ChatMessage> fetchDirectMessages(int user1, int user2, int page);
    /**
     * 获取继某条信息之后的一页消息
     *
     * @param channel 频道, 通常为群组编号或者用户编号
     * @param isDM 是否为私信消息
     * @since 起始消息
     * */
    List<ChatMessage> fetchMessages(int channel, boolean isDM, ChatMessage since);
}
