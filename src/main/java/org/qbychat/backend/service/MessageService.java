package org.qbychat.backend.service;

import org.qbychat.backend.entity.Message;

import java.util.List;

public interface MessageService {
    void addMessage(Message message);
    void removeMessage(Message message);
    void removeMessage(int messageId);

    /**
     * 获取最近的信息 (两页)
     *
     * @param channel 频道, 通常为群组编号或者用户编号
     * */
    List<Message> fetchLatestGroupMessages(int channel);
    List<Message> fetchLatestDirectMessages(int user1, int user2);
    /**
     * 获取群组消息
     *
     * @param channel 频道, 通常为群组编号或者用户编号
     * @param page 页数 (倒序)
     * */
    List<Message> fetchGroupMessages(int channel, int page);
    List<Message> fetchDirectMessages(int user1, int user2, int page);
    /**
     * 获取继某条信息之后的一页消息
     *
     * @param channel 频道, 通常为群组编号或者用户编号
     * @param isDM 是否为私信消息
     * @since 起始消息
     * */
    List<Message> fetchMessages(int channel, boolean isDM, Message since);
}
