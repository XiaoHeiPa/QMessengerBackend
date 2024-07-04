package org.qbychat.backend.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.handler.Fastjson2TypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(value = "db_messages")
public class ChatMessage implements BaseData {
    @Id(keyType = KeyType.Auto)
    private Integer id;

    private Integer sender;
    private Integer to;
    private MessageType type = MessageType.TEXT;
    @Column(value = "is_dm")
    private boolean directMessage;
    private long timestamp;

    @Column(typeHandler = Fastjson2TypeHandler.class)
    private MessageContent content;

    enum MessageType {
        TEXT,
        IMAGE
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    static class MessageContent {
        @Builder.Default
        String description = null;
        String text;
        @Builder.Default
        Integer replyTo = null;
    }
}
