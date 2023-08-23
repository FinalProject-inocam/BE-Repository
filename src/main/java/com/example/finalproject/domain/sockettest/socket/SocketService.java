package com.example.finalproject.domain.sockettest.socket;

import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOClient;
import com.example.finalproject.domain.sockettest.constants.Constants;
import com.example.finalproject.domain.sockettest.model.Message;
import com.example.finalproject.domain.sockettest.model.MessageType;
import com.example.finalproject.domain.sockettest.model.Room;
import com.example.finalproject.domain.sockettest.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocketService {


    private final MessageService messageService;


    public void sendSocketMessage(SocketIOClient senderClient, Message message, String room) {
        for (
                SocketIOClient client : senderClient.getNamespace().getRoomOperations(room).getClients()) {
            if (!client.getSessionId().equals(senderClient.getSessionId())) {
                client.sendEvent("readMsg",
                        message);
            }
        }
    }

    public void saveMessage(SocketIOClient senderClient, Message message) {
        var params = senderClient.getHandshakeData().getUrlParams();
        log.info("param : " + params.toString());
        log.info("test message : " +message.getRoom() + message.getUsername());
        Message storedMessage = messageService.saveMessage(Message.builder()
                .content(message.getContent())
                .room(message.getRoom())
                .username(message.getUsername())
                .build());
        sendSocketMessage(senderClient, storedMessage, message.getRoom());
    }

    public void saveInfoMessage(SocketIOClient senderClient, String message, String room) {
        Message storedMessage = messageService.saveMessage(Message.builder()
                .content(message)
                .room(room)
                .build());
        sendSocketMessage(senderClient, storedMessage, room);
    }

    public void requestPreviousChat(SocketIOClient senderClient, String room) {
        log.info("previoustMsg 발송");
        // 방 기록 저장소에서 이전 채팅 기록 가져오기
        List<Message> previousMessage = messageService.getMessages(room);
        log.info(senderClient.toString());
        for ( SocketIOClient client : senderClient.getNamespace().getRoomOperations(room).getClients()) {
            if (client.getSessionId().equals(senderClient.getSessionId())) {
                // 클라이언트에 이전 채팅 기록 전송
                log.info(client.toString());
                client.sendEvent("previousMsg", previousMessage);
                return;
            }
        }
    }

//    public void roomList(SocketIOClient senderClient) {
//        log.info("roomList test");
//        List<Room> roomList = roomService.getRoomList(senderClient);
//
//    }
}
