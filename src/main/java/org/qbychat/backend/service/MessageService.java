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
     * @param isDM 是否为私信消息
     * */
    List<Message> fetchLatestMessages(Integer channel, boolean isDM);
    /**
     * 获取消息
     *
     * @param channel 频道, 通常为群组编号或者用户编号
     * @param isDM 是否为私信消息
     * @param page 页数 (倒序)
     * */
    List<Message> fetchMessages(Integer channel, boolean isDM, int page);
    /**
     * 获取继某条信息之后的一页消息
     *
     * @param channel 频道, 通常为群组编号或者用户编号
     * @param isDM 是否为私信消息
     * @since 起始消息
     * */
    List<Message> fetchMessages(Integer channel, boolean isDM, Message since);
}
