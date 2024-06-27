package org.qbychat.backend.ws;

import jakarta.annotation.Resource;
import org.qbychat.backend.utils.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class JwtWebSocketHandshakeInterceptor implements HandshakeInterceptor {

    @Resource
    JwtUtils jwtUtils;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            String bearerToken = servletRequest.getServletRequest().getHeader("Authorization");
            if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                String token = bearerToken.substring(7);
                if (jwtUtils.isInvalidToken(token)) {
                    // JWT有效，可以继续握手
                    attributes.put("username", jwtUtils.getUsernameFromJwtToken(token));
                    return true;
                }
            }
        }
        // JWT无效或缺失，拒绝连接
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
