package com.example.finalproject.domain.sockettest.socket;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.example.finalproject.domain.sockettest.dto.AnswerRoomDto;
import com.example.finalproject.domain.sockettest.dto.CandidateRoomDto;
import com.example.finalproject.domain.sockettest.dto.OfferRoomDto;
import com.example.finalproject.domain.sockettest.entity.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SocketModule {
    // websocket의 컨트롤러 역할을 하는듯 합니다.

    private final SocketIOServer server;
    private final SocketService socketService;

    public SocketModule(SocketIOServer server, SocketService socketService) {
        this.server = server;
        this.socketService = socketService;
        server.addConnectListener(onConnected());
        server.addDisconnectListener(onDisconnected());
        // chat
        server.addEventListener("sendMsg", Message.class, onChatReceived());
        server.addEventListener("joinRoom", Message.class, joinRoomReceived());
        server.addEventListener("connection", Message.class, connectSocket());
        server.addEventListener("leaveRoom", Message.class, leaveRoom());
        server.addEventListener("saveMemo", Message.class, saveMemo());
        // rtc
        server.addEventListener("offer", OfferRoomDto.class, getOffer());
        server.addEventListener("answer", AnswerRoomDto.class, getAnswer());
        server.addEventListener("candidate", CandidateRoomDto.class, getCandidate());
        server.addEventListener("joinRTC", Message.class, joinRTC());

    }

    private DataListener<Message> joinRTC() {
        return (senderClient, data, ackSender) -> {
            log.info(data.toString());
            socketService.joinedRTC(senderClient, data);
        };
    }

    private DataListener<OfferRoomDto> getOffer() {
        return (senderClient, data, ackSender) -> {
            log.info(data.toString());
            socketService.sendOffer(senderClient, data);
        };
    }

    private DataListener<AnswerRoomDto> getAnswer() {
        return (senderClient, data, ackSender) -> {
            log.info(data.toString());
            socketService.sendAnswer(senderClient, data);
        };
    }

    //room 방법 이게 최선인가...
    private DataListener<CandidateRoomDto> getCandidate() {
        return (senderClient, data, ackSender) -> {
            log.info(data.toString());
            socketService.sendCandidate(senderClient, data);
        };
    }
    /*----위는 rtc--------*/

    private DataListener<Message> saveMemo() {
        return (senderClient, data, ackSender) -> {
            log.info(data.toString());
            socketService.saveMemo(data);
        };
    }

    private DataListener<Message> onChatReceived() {
        return (senderClient, data, ackSender) -> {
            log.info(data.toString());
            // 순서상 1 timemessage 저장 + read
            Message timeMessage = socketService.saveTimeMessage(data);
            socketService.sendTimeMessage(senderClient, timeMessage);
            // 2 socketmessage 저장 + read
            Message message = socketService.saveMessage(data);
            socketService.sendSocketMessage(senderClient, message);
        };
    }

    private DataListener<Message> joinRoomReceived() {
        return (senderClient, data, ackSender) -> {
            String room = data.getRoom();
            log.info("Socket ID[{}] - room[{}]  Connected to socket and join room",
                    senderClient.getSessionId().toString(), room);
            senderClient.joinRoom(room);
            String username = data.getUsername();
            socketService.checkRoom(room);
            socketService.joinAdmin(room, username);
            socketService.requestPreviousChat(senderClient, room);
            socketService.roomInfo(senderClient, room, username);
            log.info("previousChat 발송완료");
        };
    }

    private DataListener<Message> connectSocket() {
        return (senderClient, data, ackSender) -> {
            log.info(senderClient.getHandshakeData().getHttpHeaders().get("Authorization"));
            String username = data.getUsername();
            senderClient.joinRoom(username);
            socketService.getRoomList(senderClient, username);
            log.info("roomList 발송완료");
        };
    }

    private DataListener<Message> leaveRoom() {
        return (senderClient, data, ackSender) -> {
            log.info("방 나가기 시도 : " + data.getUsername() + " from " + data.getRoom());
            String room = data.getRoom();
            String username = data.getUsername();
            socketService.leaveRoom(room, username);
            Message message = socketService.saveLeaveMessage(room, username);
            socketService.sendLeaveMessage(senderClient, message);
            socketService.leaveAdmin(room, username);
            socketService.getRoomList(senderClient, username);
            log.info("방나가기 성공");
        };
    }


    private ConnectListener onConnected() {
        return (client) -> {
            log.info("Socket ID[{}]   Connected to chat module through", client.getSessionId().toString());
        };

    }

    private DisconnectListener onDisconnected() {
        return client -> {
            log.info("Socket ID[{}]  discnnected to chat module through", client.getSessionId().toString());
        };
    }


}
