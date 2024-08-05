package org.qbychat.backend.config;

import org.qbychat.backend.ws.ClientAuthHandler;
import org.qbychat.backend.ws.QMessengerHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handlerMessenger(), "/ws/messenger");
        registry.addHandler(handlerClientAuth(), "/ws/mc");
    }

    @Bean
    public ClientAuthHandler handlerClientAuth() {
        return new ClientAuthHandler();
    }

    @Bean
    public WebSocketHandler handlerMessenger() {
        return new QMessengerHandler();
    }
}
