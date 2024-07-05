package org.qbychat.backend.ws;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.qbychat.backend.entity.Account;
import org.qbychat.backend.entity.Message;
import org.qbychat.backend.entity.Group;
import org.qbychat.backend.service.impl.AccountServiceImpl;
import org.qbychat.backend.service.impl.FriendsServiceImpl;
import org.qbychat.backend.service.impl.GroupsServiceImpl;
import org.qbychat.backend.service.impl.MessageServiceImpl;
import org.qbychat.backend.ws.entity.*;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
public class QMessengerHandler extends AuthedTextHandler {
    @Resource
    FriendsServiceImpl friendsService;

    @Resource
    AccountServiceImpl accountService;
    @Resource
    GroupsServiceImpl groupsService;
    @Resource
    MessageServiceImpl messageService;

    public static ConcurrentHashMap<Integer, WebSocketSession> connections = new ConcurrentHashMap<>();

    @Override
    protected void afterAuthorization(@NotNull WebSocketSession session, Account account) {
        connections.put(account.getId(), session);
    }

    @Override
    protected void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        Request request = JSON.parseObject(message.getPayload(), Request.class);
        String method = request.getMethod();
        Account account = getUser(session);

        switch (method) {
            case RequestType.SEND_MESSAGE -> {
                Message chatMessage = JSON.parseObject(request.getDataJson(), Message.class);
                // send message
                // 找到目标并发送
                chatMessage.setSender(account.getId());
                Response msgResponse = chatMessage.toResponse();
                messageService.addMessage(chatMessage);
                // direct message
                if (!chatMessage.isDirectMessage() && accountService.hasUser(chatMessage.getTo())) {
                    WebSocketSession targetSession = connections.get(chatMessage.getTo());
                    if (targetSession != null) {
                        targetSession.sendMessage(new TextMessage(msgResponse.toJson()));
                    }
                } else if (chatMessage.isDirectMessage() && groupsService.hasGroup(chatMessage.getTo())) {
                    Group group = groupsService.getGroupById(chatMessage.getTo());
                    for (Integer memberId : group.getMembers()) {
                        WebSocketSession targetSession = connections.get(memberId);
                        if (targetSession != null) {
                            targetSession.sendMessage(new TextMessage(msgResponse.toJson()));
                        }
                    }
                }
            } case RequestType.ADD_FRIEND -> {
                RequestAddFriend friendRequest = JSON.parseObject(request.getDataJson(), RequestAddFriend.class);
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
            } case RequestType.ACCEPT_FRIEND_REQUEST -> {
                Integer target = JSON.parseObject(request.getDataJson(), Integer.class);
                friendsService.addFriend(getUser(session), accountService.findAccountById(target));
            } case RequestType.FETCH_LATEST_MESSAGES -> {
                RequestFetchLatestMessages data = JSON.parseObject(request.getDataJson(), RequestFetchLatestMessages.class);
                List<Message> messages = messageService.fetchLatestMessages(data.getChannel(), data.isDirectMessage());
                for (Message chatMessage : messages) {
                    session.sendMessage(chatMessage.toWSTextMessage()); // 排序在客户端进行
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) throws Exception {
        Account account = getUser(session);
        if (account == null) {
            return; // not authed
        }
        log.info("User {} has disconnected from {}", account.getId(), this.getClass().getName());
        connections.remove(account.getId());
    }
}
