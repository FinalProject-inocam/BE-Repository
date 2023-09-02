package com.example.finalproject.domain.sockettest.socket;

import com.corundumstudio.socketio.SocketIOClient;
import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.auth.repository.UserRepository;
import com.example.finalproject.domain.mypage.dto.MypageResDto;
import com.example.finalproject.domain.purchases.dto.response.PurchasesResponseDto;
import com.example.finalproject.domain.purchases.repository.PurchasesRepository;
import com.example.finalproject.domain.sockettest.constants.Constants;
import com.example.finalproject.domain.sockettest.dto.*;
import com.example.finalproject.domain.sockettest.model.Memo;
import com.example.finalproject.domain.sockettest.model.Message;
import com.example.finalproject.domain.sockettest.repository.MemoRepository;
import com.example.finalproject.domain.sockettest.service.MemoService;
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
    private final UserRepository userRepository;
    private final PurchasesRepository purchasesRepository;
    private final MemoService memoService;


    public void sendSocketMessage(SocketIOClient senderClient, Message message) {
        for (SocketIOClient client : allClientInRoomWithOutSelf(senderClient, message.getRoom())) {
            client.sendEvent("readMsg", message);
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
    }

    public void roomInfo(SocketIOClient senderClient, String room, String username) {
        log.info("room관련 정보 발송");
        // admin한테만 어떻게 보내지...?
        if (!username.equals("admin")) {
            return;
        }
        String targetusername = room.replace("admin", "").replace("!", "");
        User user = userRepository.findByNickname(targetusername);
        MypageResDto mypageResDto = new MypageResDto(user);
        List<PurchasesResponseDto> purchasesResponseDtoList = purchasesRepository.findAllByUser(user)
                .stream()
                .map(PurchasesResponseDto::new)
                .toList();
        String memoStr = "";
        Memo memo = memoService.getMemo(room).orElse(null);
        if (memo != null) {
            memoStr = memo.getMemo();
        }

        RoomInfoResponseDto roomInfoResponseDto = new RoomInfoResponseDto(mypageResDto, purchasesResponseDtoList, memoStr);

        senderClient.sendEvent("roomInfo", roomInfoResponseDto);
    }

    public void getRoomList(SocketIOClient senderClient, String username) {
        log.info("roomList 준비");
        List<RoomResponseDto> roomList = roomService.getRoomListContainUsername(username);
        senderClient.sendEvent("connected", roomList);
    }

    public void checkRoom(String room) {
        log.info("room 존재 여부 확인");
        if (!roomService.isRoom(room)) {
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
    }

    public Message saveLeaveMessage(String room, String username) {
        Message storedMessage = messageService.saveMessage(Message.builder()
                .content(String.format(Constants.PEEROUT_MESSAGE, username))
                .room(room)
                .username("server")
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
            if (candidateRoomDto.getCandidate() != null) {
                client.sendEvent("getCandidate", candidateRoomDto.getCandidate());
            }
        }
    }

    public void sendAnswer(SocketIOClient senderClient, AnswerRoomDto answerRoomDto) {
        for (SocketIOClient client : allClientInRoomWithOutSelf(senderClient, answerRoomDto.getRoom())) {
            log.info("send check : " + answerRoomDto.toString());
            if (answerRoomDto.getAnswer() != null) {
                client.sendEvent("getAnswer", answerRoomDto.getAnswer());
            }
        }
    }

    public void sendOffer(SocketIOClient senderClient, OfferRoomDto offerRoomDto) {
        for (SocketIOClient client : allClientInRoomWithOutSelf(senderClient, offerRoomDto.getRoom())) {
            log.info("offer check : " + offerRoomDto.toString());
            if (offerRoomDto.getOffer() != null) {
                client.sendEvent("getOffer", offerRoomDto.getOffer());
            }
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

    public void saveMemo(Message message) {
        if(!message.getUsername().equals("admin")) {
            return;
        }
        String room = message.getRoom();
        String memoText = message.getContent();
        Memo memo = new Memo(room, memoText);
        memoService.saveMemo(memo);
    }
}
