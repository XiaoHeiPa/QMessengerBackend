package org.qbychat.backend.ws;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.qbychat.backend.entity.Account;
import org.qbychat.backend.entity.RestBean;
import org.qbychat.backend.service.impl.AccountServiceImpl;
import org.qbychat.backend.utils.JwtUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;


@Log4j2
public abstract class AuthedTextHandler extends TextWebSocketHandler {
    @Resource
    private JwtUtils jwtUtils;

    @Resource
    private AccountServiceImpl accountService;

    /**
     * 鉴权
     */
    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) throws Exception {
        Account user = getUser(session);
        if (user != null) {
            Account account = accountService.findAccountByNameOrEmail(user.getUsername());
            log.info("User " + account.getId() + " has connected to " + this.getClass().getName());
            session.sendMessage(new TextMessage(RestBean.success(account).toJson()));
        } else {
            session.sendMessage(new TextMessage(RestBean.forbidden("Valid token").toJson()));
            session.close();
        }
        // 接下来
        // 通过 session.isOpen() 来判断是否验证成功
        // (通常不需要写)
    }

    protected Account getUser(@NotNull WebSocketSession session) {
        List<String> authorization = session.getHandshakeHeaders().get("Authorization");
        DecodedJWT jwt = jwtUtils.resolveJwt(Objects.requireNonNull(authorization).get(0));
        if (jwt != null) {
            return accountService.findAccountByNameOrEmail(jwtUtils.toUser(jwt).getUsername());
        }
        return null;
    }
}
