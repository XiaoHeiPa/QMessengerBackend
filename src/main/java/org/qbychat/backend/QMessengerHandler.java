package org.qbychat.backend;

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
        session.sendMessage(new TextMessage(RestBean.success().toJson()));
    }

    @Override
    protected void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) throws Exception {

        super.handleTextMessage(session, message);
        log.info(message.getPayload());
    }

    @Override
    protected void handlePongMessage(@NotNull WebSocketSession session, @NotNull PongMessage message) throws Exception {
        super.handlePongMessage(session, message);
        List<String> authorization = session.getHandshakeHeaders().get("Authorization");
        if (jwtUtils.invalidateJwt(Objects.requireNonNull(authorization).get(0))) {
            Account account = accountService.findAccountById(jwtUtils.getId((DecodedJWT) authorization));
            List<String> sessionSecret = session.getHandshakeHeaders().get("Secret");
            if (sessionSecret != null) {
                String secret = sessionSecret.toString();
                redisTemplate.opsForValue().set(account.getUsername(), secret);
            } else {
                session.sendMessage(new TextMessage("Your http headers don't have secret. Please get info from our api doc."));
            }
        }
    }
}
