package org.qbychat.backend;

import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.qbychat.backend.entity.RestBean;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.security.Principal;

@Log4j2
public class QMessengerHandler extends TextWebSocketHandler {
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        session.sendMessage(new TextMessage(RestBean.success().toJson()));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        log.info(message.getPayload());
    }

    @Override
    protected void handlePongMessage(@NotNull WebSocketSession session, @NotNull PongMessage message) throws Exception {
        super.handlePongMessage(session, message);
    }
}
