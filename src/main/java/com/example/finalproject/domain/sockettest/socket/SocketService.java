package com.example.finalproject.domain.sockettest.socket;

import com.corundumstudio.socketio.SocketIOClient;
import com.example.finalproject.domain.sockettest.constants.Constants;
import com.example.finalproject.domain.sockettest.dto.*;
import com.example.finalproject.domain.sockettest.model.Message;
import com.example.finalproject.domain.sockettest.service.MessageService;
import com.example.finalproject.domain.sockettest.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocketService {


    private final MessageService messageService;
    private final RoomService roomService;


    public void sendSocketMessage(SocketIOClient senderClient, Message message, String room) {
        for (
                SocketIOClient client : senderClient.getNamespace().getRoomOperations(room).getClients()) {
            if (!client.getSessionId().equals(senderClient.getSessionId())) {
                client.sendEvent("readMsg", message);
            }
        }
    }

    public Message saveMessage(Message message) {
//        var params = senderClient.getHandshakeData().getUrlParams();
//        log.info("param : " + params.toString());
        log.info("test message : " + message.getRoom() + message.getUsername());
        Message storedMessage = messageService.saveMessage(Message.builder()
                .content(message.getContent())
                .room(message.getRoom())
                .username(message.getUsername())
                .build());
        return storedMessage;
    }

//    public void saveInfoMessage(SocketIOClient senderClient, String message, String room) {
//        Message storedMessage = messageService.saveMessage(Message.builder()
//                .content(message)
//                .room(room)
//                .build());
//        sendSocketMessage(senderClient, storedMessage, room);
//    }

    public void requestPreviousChat(SocketIOClient senderClient, String room) {
        log.info("previoustMsg 발송");
        // 방 기록 저장소에서 이전 채팅 기록 가져오기
        List<Message> previousMessage = messageService.getMessages(room);
        senderClient.sendEvent("previousMsg", previousMessage);
//        log.info(senderClient.toString());
//        for (SocketIOClient client : senderClient.getNamespace().getRoomOperations(room).getClients()) {
//            if (client.getSessionId().equals(senderClient.getSessionId())) {
//                // 클라이언트에 이전 채팅 기록 전송
//                log.info(client.toString());
//                client.sendEvent("previousMsg", previousMessage);
//                return;
//            }
//        }
    }

    public void getRoomList(SocketIOClient senderClient, String username) {
        log.info("roomList 준비");
        // 이제 나간 사람 어떻게 또 조건에 추가하지
        List<RoomResponseDto> roomList = roomService.getRoomListContainUsername(username);
        senderClient.sendEvent("connected", roomList);
    }

    public void checkRoom(String room) {
        log.info("room 존재 여부 확인");
        if(!roomService.isRoom(room)) {
            log.info("새로운 방 생성");
            roomService.createRoom(room);
        } else {
            log.info("peer 다시 등록");
            roomService.rejoinRoom(room);
        }
    }

    public void leaveRoom(String room, String username) {
        // 전체적으로 room유효검사 부분을 추가해야할듯 하다.
        log.info("방 나가기 처리중");
        roomService.leaveRoom(room, username);
//        log.info("leave message : " + username + " from " + room);
//        Message storedMessage = messageService.saveMessage(Message.builder()
//                .content(String.format(Constants.PEEROUT_MESSAGE, username))
//                .room(room)
//                .username(username)
//                .build());
//        senderClient.sendEvent("peerOut", storedMessage);
    }

    public Message saveLeaveMessage(String room, String username) {
        Message storedMessage = messageService.saveMessage(Message.builder()
                .content(String.format(Constants.PEEROUT_MESSAGE, username))
                .room(room)
                .username(username)
                .build());
        return storedMessage;
    }

    public void sendLeaveMessage(SocketIOClient senderClient, Message message) {
        for (SocketIOClient client : allClientInRoomWithOutSelf(senderClient, message.getRoom())) {
                client.sendEvent("peerOut", message);
        }
    }

    public void sendCandidate(SocketIOClient senderClient, CandidateRoomDto candidateRoomDto) {
        for (SocketIOClient client : allClientInRoomWithOutSelf(senderClient, candidateRoomDto.getRoom())) {
            log.info("candidate check : " + candidateRoomDto.toString());
            client.sendEvent("getCandidate", candidateRoomDto.getCandidate());
        }
    }

    public void sendAnswer(SocketIOClient senderClient, AnswerRoomDto answerRoomDto) {
        for (SocketIOClient client : allClientInRoomWithOutSelf(senderClient, answerRoomDto.getRoom())) {
            log.info("send check : " + answerRoomDto.toString());
            client.sendEvent("getAnswer", answerRoomDto.getAnswer());
        }
    }

    public void sendOffer(SocketIOClient senderClient, OfferRoomDto offerRoomDto) {
        for (SocketIOClient client : allClientInRoomWithOutSelf(senderClient, offerRoomDto.getRoom())) {
            log.info("offer check : " + offerRoomDto.toString());
            client.sendEvent("getOffer", offerRoomDto.getOffer());
        }
    }

    public List<SocketIOClient> allClientInRoomWithOutSelf(SocketIOClient senderClient, String room) {
        return senderClient.getNamespace()
                .getRoomOperations(room)
                .getClients()
                .stream()
                .filter(client -> !client.getSessionId().equals(senderClient.getSessionId()))
                .toList();
    }

    public void joinedRTC(SocketIOClient senderClient, Message message) {
        for (SocketIOClient client : allClientInRoomWithOutSelf(senderClient, message.getRoom())) {
            log.info("joinedRTC발송");
            client.sendEvent("joinedRTC", "상대방이 접속했습니다.");
        }
    }

}
