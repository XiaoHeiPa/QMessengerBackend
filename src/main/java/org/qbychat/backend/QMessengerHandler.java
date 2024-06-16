package org.qbychat.backend;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Log4j2
public class QMessengerHandler extends TextWebSocketHandler {
    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        super.handlePongMessage(session, message);
        log.info("Pong! " + message);
    }
}
