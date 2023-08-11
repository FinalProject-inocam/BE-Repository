package com.example.finalproject.domain.chat.config;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j(topic = "websocket")
public class SocketIOConfig {

    @Value("${socketio.host}")
    private String host;

    @Value("${socketio.port}")
    private Integer port;

    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname(host);
        config.setPort(port);

        // CORS 설정 추가
        config.setOrigin("*");

        config.setAuthorizationListener(handshakeData -> {
            String token = handshakeData.getSingleUrlParam("token");
            return validateToken(token);
        });

        return new SocketIOServer(config);
    }

    private boolean validateToken(String token) {
        return true;
    }
}