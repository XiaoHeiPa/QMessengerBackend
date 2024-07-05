package org.qbychat.backend.ws;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.qbychat.backend.entity.Account;
import org.qbychat.backend.entity.RestBean;
import org.qbychat.backend.entity.dto.AccountDTO;
import org.qbychat.backend.service.impl.AccountServiceImpl;
import org.qbychat.backend.utils.JwtUtils;
import org.qbychat.backend.ws.entity.Response;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;


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
        Account account = getUser(session);
        if (account != null) {
            if (!account.isActive()) {
                session.sendMessage(new TextMessage(RestBean.failure(403, "You're now banned, so you cannot connect to the ws server.").toJson()));
                session.close();
                return;
            }
            log.info("User {} has connected to {}", account.getId(), this.getClass().getName());
            AccountDTO authorizeVO = account.asViewObject(AccountDTO.class);
            session.sendMessage(new TextMessage(Response.USER_INFO.setData(authorizeVO).toJson()));
        } else {
            session.sendMessage(new TextMessage(Response.builder().method(Response.USER_INFO.method).hasError(true).data("Please provide a token").build().toJson()));
            session.close();
            return;
        }
        afterAuthorization(session, account);
    }

    protected void afterAuthorization(@NotNull WebSocketSession session, Account account) throws Exception {}

    protected Account getUser(@NotNull WebSocketSession session) {
        List<String> authorization = session.getHandshakeHeaders().get("Authorization");
        if (authorization == null) {
            return null;
        }
        DecodedJWT jwt = jwtUtils.resolveJwt(authorization.get(0));
        if (jwt != null) {
            return accountService.findAccountByNameOrEmail(jwtUtils.toUser(jwt).getUsername());
        }
        return null;
    }
}
