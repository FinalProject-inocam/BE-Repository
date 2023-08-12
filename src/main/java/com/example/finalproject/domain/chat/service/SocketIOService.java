package com.example.finalproject.domain.chat.service;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.auth.repository.UserRepository;
import com.example.finalproject.domain.chat.dto.MessageDto;
import com.example.finalproject.domain.chat.entity.Message;
import com.example.finalproject.domain.chat.entity.Room;
import com.example.finalproject.domain.chat.repository.MessageRepository;
import com.example.finalproject.domain.chat.repository.RoomRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@Slf4j(topic = "web socket")
public class SocketIOService {

    @Autowired
    private SocketIOServer socketIOServer;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @PostConstruct
    public void start() {
        socketIOServer.addConnectListener(this::handleClientConnect);
        socketIOServer.addEventListener("chat_message", MessageDto.class, this::handleChatMessage);
        socketIOServer.start();
    }

    private void handleClientConnect(SocketIOClient client) {
        String roomId = client.getHandshakeData().getSingleUrlParam("roomId");
        if (roomId != null && !roomId.isEmpty()) {
            client.joinRoom(roomId);
            log.info("Client joined to room: " + roomId);
        } else {
            log.warn("Client tried to connect without a roomId");
        }
        // 기타 연결 로직 (필요하다면)
    }

    private void handleChatMessage(SocketIOClient client, MessageDto messageDto, AckRequest ackRequest) {
        try {
            Message message = saveMessageToDatabase(messageDto);
            broadcastMessage(messageDto);
            log.info("is it ok2");
        } catch (Exception e) {
            log.error("Error handling chat message", e);
        }
    }

    private Message saveMessageToDatabase(MessageDto messageDto) {
        User user = userRepository.findByNickname(messageDto.getNickname());
        Room room = roomRepository.findById(messageDto.getRoomId()).orElse(null);

        if (user == null || room == null) {
            throw new RuntimeException("User or Room not found!");
        }

        Message message = new Message();
        message.setUser(user);
        message.setRoom(room);
        message.setContent(messageDto.getContent());
        message.setTimestamp(LocalDateTime.now());

        return messageRepository.save(message);
    }

    private void broadcastMessage(MessageDto messageDto) {
        log.info("room Id : " + messageDto.getRoomId());
        socketIOServer.getRoomOperations(messageDto.getRoomId().toString()).sendEvent("chat_message", messageDto);
        log.info("is it ok?");
    }

    @PreDestroy
    public void stop() {
        socketIOServer.stop();
    }
}