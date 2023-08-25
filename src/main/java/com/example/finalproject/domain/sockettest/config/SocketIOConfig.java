package com.example.finalproject.domain.sockettest.config;

import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SocketIOConfig {

    @Value("${socket-client.origin}")
    private String origin;

    @Value("${socket-server.port}")
    private Integer port;

    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
//        config.setHostname(host);
        config.setPort(port);
        config.setOrigin(origin);
        //  config.setContext("/socket.io");
        return new SocketIOServer(config);
    }

}
