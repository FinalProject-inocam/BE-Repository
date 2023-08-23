package com.example.finalproject.domain.sockettest.socket;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.example.finalproject.domain.sockettest.constants.Constants;
import com.example.finalproject.domain.sockettest.dto.CandidateDto;
import com.example.finalproject.domain.sockettest.dto.OfferAndAnswerDto;
import com.example.finalproject.domain.sockettest.model.Message;
import com.example.finalproject.domain.sockettest.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

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
        // rtc
        server.addEventListener("offer", OfferAndAnswerDto.class, getOffer());
        server.addEventListener("answer", OfferAndAnswerDto.class, getAnswer());
        server.addEventListener("candidate", CandidateDto.class, getCandidate());

    }

    private DataListener<OfferAndAnswerDto> getOffer() {
        return (senderClient, data, ackSender) -> {
            log.info(data.toString());
            socketService.sendOffer(senderClient, data, data.getRoom());
        };
    }

    private DataListener<OfferAndAnswerDto> getAnswer() {
        return (senderClient, data, ackSender) -> {
            log.info(data.toString());
            socketService.sendAnswer(senderClient, data, data.getRoom());
        };
    }

    //room 방법 이게 최선인가...
    private DataListener<CandidateDto> getCandidate() {
        return (senderClient, data, ackSender) -> {
            log.info(data.toString());
            socketService.sendCandidate(senderClient, data, data.getRoom());
        };
    }
/*----위는 rtc--------*/

    private DataListener<Message> onChatReceived() {
        return (senderClient, data, ackSender) -> {
            log.info(data.toString());
            Message message = socketService.saveMessage(data);
            socketService.sendSocketMessage(senderClient, message, message.getRoom());
        };
    }

    private DataListener<Message> joinRoomReceived() {
        return (senderClient, data, ackSender) -> {
            String room = data.getRoom();
            log.info("Socket ID[{}] - room[{}]  Connected to socket and join room",
                    senderClient.getSessionId().toString(), room);
            senderClient.joinRoom(room);
            socketService.checkRoom(room);
            socketService.requestPreviousChat(senderClient, room);
            log.info("previousChat 발송완료");
        };
    }

    private DataListener<Message> connectSocket() {
        return (senderClient, data, ackSender) -> {
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
            socketService.sendLeaveMessage(senderClient, message, room);
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
