package org.qbychat.backend.ws;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.qbychat.backend.entity.Account;
import org.qbychat.backend.service.AccountService;
import org.qbychat.backend.service.impl.AccountServiceImpl;
import org.qbychat.backend.service.impl.FriendsServiceImpl;
import org.qbychat.backend.utils.Const;
import org.qbychat.backend.ws.entity.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Log4j2
public class QMessengerHandler extends AuthedTextHandler {
    @Resource
    FriendsServiceImpl friendsService;

    @Resource
    AccountServiceImpl accountService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public static ConcurrentHashMap<Integer, WebSocketSession> connections = new ConcurrentHashMap<>();

    @Override
    protected void afterAuthed(@NotNull WebSocketSession session, Account account) throws Exception {
        connections.put(account.getId(), session);
        // 发送离线时收到的消息
        Object cache0 = redisTemplate.opsForValue().get(Const.CACHED_MESSAGE + account.getId());
        if (cache0 != null) {
            List<ChatMessage> caches = (List<ChatMessage>) cache0;
            for (ChatMessage chatMessage : caches) {
                Response msgResponse = Response.CHAT_MESSAGE.setData(chatMessage);
                session.sendMessage(new TextMessage(msgResponse.toJson()));
            }
        }
    }


    @Override
    protected void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        Request request = JSON.parseObject(message.getPayload(), Request.class);
        String method = request.getMethod();
        Account account = getUser(session);

        switch (method) {
            case RequestType.SEND_MESSAGE -> {
                ChatMessage chatMessage = JSON.parseObject(request.getDataJson(), ChatMessage.class);
                log.info("Message from {} to {}: {}", account.getUsername(), chatMessage.getTo(), chatMessage.getContent());
                // send message
                // todo 实现群组, 离线消息, fcm

                // 找到目标并发送
                Account to = accountService.findAccountByNameOrEmail(chatMessage.getTo());
                chatMessage.setTimestamp(Calendar.getInstance().getTimeInMillis());
                Response msgResponse = Response.CHAT_MESSAGE.setData(chatMessage);
                boolean isTargetOnline = false;
                for (Integer id : connections.keySet()) {
                    WebSocketSession s = connections.get(id);
                    if (id.equals(to.getId())) {
                        s.sendMessage(new TextMessage(msgResponse.toJson()));
                        isTargetOnline = true;
                    }
                }
                if (!isTargetOnline) {
                    // 先将消息缓存
                    Object cache0 = redisTemplate.opsForValue().get(Const.CACHED_MESSAGE + to.getId());
                    List<ChatMessage> caches;
                    if (cache0 == null) {
                        caches = new ArrayList<>();
                    } else {
                        caches = (List<ChatMessage>) cache0; // wtf unchecked cast
                    }
                    caches.add(chatMessage);
                    // 只缓存<timeout>天
                    redisTemplate.opsForValue().set(Const.CACHED_MESSAGE + to.getId(), caches, 7, TimeUnit.DAYS);
                }
            }
            case RequestType.ADD_FRIEND -> {
                AddFriendRequest friendRequest = JSON.parseObject(request.getDataJson(), AddFriendRequest.class);
                Integer target = friendRequest.getTarget();
                friendRequest.setFrom(account.getId());
                if (friendsService.hasFriend(account, accountService.findAccountById(target))) {
                    session.sendMessage(new TextMessage(Response.HAS_FRIEND.toJson()));
                    return;
                }
                // 发送请求
                session.sendMessage(new TextMessage(Response.FRIEND_REQUEST_SENT.toJson()));
                // find target session
                WebSocketSession targetWebsocket = connections.get(target);
                targetWebsocket.sendMessage(new TextMessage(Response.FRIEND_REQUEST.setData(friendRequest).toJson()));
            }
            case RequestType.ACCEPT_FRIEND_REQUEST -> {
                Integer target = JSON.parseObject(request.getDataJson(), Integer.class);
                log.info(target);
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
