package org.qbychat.backend.ws;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.qbychat.backend.entity.Account;
import org.qbychat.backend.entity.RestBean;
import org.qbychat.backend.service.impl.AccountServiceImpl;
import org.qbychat.backend.utils.JwtUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Objects;

@Log4j2
public class QMessengerHandler extends TextWebSocketHandler {
    @Resource
    private JwtUtils jwtUtils;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private AccountServiceImpl accountService;

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        List<String> authorization = session.getHandshakeHeaders().get("Authorization");
        DecodedJWT jwt = jwtUtils.resolveJwt(Objects.requireNonNull(authorization).get(0));
        if (jwt != null) {
            Account account = accountService.findAccountByNameOrEmail(jwtUtils.toUser(jwt).getUsername());
            session.sendMessage(new TextMessage(RestBean.success(account).toJson()));
            return;
        }
        session.sendMessage(new TextMessage(RestBean.forbidden("Valid token").toJson()));
        session.close();
    }

    @Override
    protected void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) throws Exception {

        super.handleTextMessage(session, message);
        log.info(message.getPayload());
    }
}
