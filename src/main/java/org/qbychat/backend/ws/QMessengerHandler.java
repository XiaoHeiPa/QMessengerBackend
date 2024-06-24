package org.qbychat.backend.ws;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.qbychat.backend.entity.Account;
import org.qbychat.backend.service.AccountService;
import org.qbychat.backend.service.impl.AccountServiceImpl;
import org.qbychat.backend.ws.entity.ChatMessage;
import org.qbychat.backend.ws.entity.Request;
import org.qbychat.backend.ws.entity.RequestType;
import org.qbychat.backend.ws.entity.Response;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
public class QMessengerHandler extends AuthedTextHandler {
    @Resource
    AccountServiceImpl accountService;

    public static ConcurrentHashMap<Integer, WebSocketSession> connections = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        if (session.isOpen()) {
            connections.put(getUser(session).getId(), session);
        }
    }


    @Override
    protected void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        Request request = JSON.parseObject(message.getPayload(), Request.class);
        String method = request.getMethod();
        Account account = getUser(session);

        ChatMessage chatMessage = JSON.parseObject(request.getDataJson(), ChatMessage.class);
        if (method.equals(RequestType.SEND_MESSAGE)) {
            log.info("Message from {} to {}: {}", account.getUsername(), chatMessage.getTo(), chatMessage.getContent());
            // send message
            // todo 实现群组, 离线消息, fcm

            // 找到目标并发送
            Account to = accountService.findAccountByNameOrEmail(chatMessage.getTo());
            chatMessage.setTimestamp(Calendar.getInstance().getTimeInMillis());
            Response msgResponse = new Response("chat-message", chatMessage);
            for (Integer id : connections.keySet()) {
                WebSocketSession s = connections.get(id);
                if (id.equals(to.getId())) {
                    s.sendMessage(new TextMessage(msgResponse.toJson()));
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) throws Exception {
        Account account = getUser(session);
        log.info("User {} has disconnected from {}", account.getId(), this.getClass().getName());
        connections.remove(account.getId());
    }
}
