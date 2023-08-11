package com.example.finalproject.domain.chat.service;

import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "websocket")
public class SocketIOService {
    @Autowired
    private SocketIOServer socketIOServer;

    @PostConstruct
    public void start() {
        socketIOServer.addConnectListener(client -> {
            // 연결될 때의 로직
        });

        socketIOServer.addEventListener("chat_message", String.class, (client, data, ackRequest) -> {
            // 받은 메시지를 모든 클라이언트에게 방송합니다.
            socketIOServer.getBroadcastOperations().sendEvent("chat_message", data);
        });

        socketIOServer.start();
    }

    @PreDestroy
    public void stop() {
        socketIOServer.stop();
    }
}