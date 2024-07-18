package org.qbychat.backend.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.handler.Fastjson2TypeHandler;
import lombok.*;
import org.qbychat.backend.ws.entity.Response;
import org.springframework.web.socket.TextMessage;

import java.util.Calendar;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Table(value = "db_messages")
public class ChatMessage implements BaseData {
    @Id(keyType = KeyType.Auto)
    private Integer id;

    private Integer sender;
    private Account senderInfo;
    private Integer to;
    private MessageType type = MessageType.TEXT;
    @Column(value = "is_dm")
    private boolean directMessage;
    private long timestamp;

    @Column(typeHandler = Fastjson2TypeHandler.class)
    private MessageContent content;

    public Response toResponse() {
        this.setTimestamp(Calendar.getInstance().getTimeInMillis());
        return Response.CHAT_MESSAGE.setData(this);
    }

    public TextMessage toWSTextMessage() {
        return new TextMessage(this.toResponse().toJson());
    }

    enum MessageType {
        TEXT,
        IMAGE
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MessageContent {
        @Builder.Default
        String description = null;
        String text;
        @Builder.Default
        Integer replyTo = null;
    }
}
