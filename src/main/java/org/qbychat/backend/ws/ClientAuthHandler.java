package org.qbychat.backend.ws;

import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.qbychat.backend.entity.Account;
import org.qbychat.backend.ws.entity.Response;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

@Log4j2
public class ClientAuthHandler extends AuthedTextHandler {

    public static ConcurrentHashMap<Integer, WebSocketSession> connections = new ConcurrentHashMap<>();

    @Override
    protected void afterAuthorization(@NotNull WebSocketSession session, @NotNull Account account) throws Exception {
        if (connections.containsKey(account.getId())) {
            // Don't share your account
            WebSocketSession otherClient = connections.get(account.getId());
            TextMessage message = new TextMessage(Response.MULTI_LOGIN.toJson());
            otherClient.sendMessage(message);
            session.sendMessage(message);
            connections.remove(account.getId()).close();
            session.close();
            return;
        }
        connections.put(account.getId(), session);
    }

    @Override
    protected void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) throws Exception {

    }
}
