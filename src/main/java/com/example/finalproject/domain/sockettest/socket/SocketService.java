package com.example.finalproject.domain.sockettest.socket;

import com.corundumstudio.socketio.BroadcastOperations;
import com.corundumstudio.socketio.SocketIOClient;
import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.auth.repository.UserRepository;
import com.example.finalproject.domain.purchases.repository.PurchasesRepository;
import com.example.finalproject.domain.sockettest.constants.Constants;
import com.example.finalproject.domain.sockettest.dto.*;
import com.example.finalproject.domain.sockettest.entity.Memo;
import com.example.finalproject.domain.sockettest.entity.Message;
import com.example.finalproject.domain.sockettest.service.MemoService;
import com.example.finalproject.domain.sockettest.service.MessageService;
import com.example.finalproject.domain.sockettest.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
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


    public void sendTimeMessage(SocketIOClient senderClient, Message message) {
        if (message == null) {
            return;
        }
        log.info("time message 전송");
        BroadcastOperations allClientInRoom = allClientInRoom(senderClient, message.getRoom());
        allClientInRoom.sendEvent("readMsg", message);
    }

    public void sendSocketMessage(SocketIOClient senderClient, Message message) {
        for (SocketIOClient client : allClientInRoomWithOutSelf(senderClient, message.getRoom())) {
            client.sendEvent("readMsg", message);
        }
    }

    public Message saveTimeMessage(Message message) {
        Message lastMessage = messageService.getLastMessage(message.getRoom());
        LocalDate todayStart = LocalDate.now();
        // 이전 메세지가 있고 그 메세지날짜가 오늘과 같은 경우 생성하지 않음
        if (lastMessage != null) {
            LocalDate lastMessageTime = lastMessage.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (!lastMessageTime.isBefore(todayStart)) {
                return null;
            }
        }

        log.info("time message : " + todayStart);
        Message timeMessage = messageService.saveMessage(Message.builder()
                .content(todayStart.toString())
                .room(message.getRoom())
                .username("date")
                .build());
        return timeMessage;
    }

    public Message saveMessage(Message message) {
        log.info("test message : " + message.getRoom() + message.getUsername());
        Message storedMessage = messageService.saveMessage(Message.builder()
                .content(message.getContent())
                .room(message.getRoom())
                .username(message.getUsername())
                .build());
        return storedMessage;
    }

    public void requestPreviousChat(SocketIOClient senderClient, String room) {
        // 방 기록 저장소에서 이전 채팅 기록 가져오기
        List<Message> previousMessage = messageService.getMessages(room);
        senderClient.sendEvent("previousMsg", previousMessage);
        log.info("previoustMsg 발송");
    }

    public void roomInfo(SocketIOClient senderClient, String room, String username) {
        if (!username.equals("E001")) {
            return;
        }
        String[] nameArray = room.split("!");
        String targetusername = nameArray[1].equals(username) ? nameArray[0] : nameArray[1];
        User user = userRepository.findByNickname(targetusername);
        UserInfoDto userInfoDto = new UserInfoDto(user);
        List<PurchaseResponseDtoSocket> purchasesResponseDtoList = purchasesRepository.findAllByUser(user)
                .stream()
                .map(PurchaseResponseDtoSocket::new)
                .toList();

        Memo memo = memoService.getMemo(room).orElseThrow(()
                -> new NullPointerException("memo가 없습니다."));
        String memoStr = memo.getMemo();

        RoomInfoResponseDto roomInfoResponseDto = new RoomInfoResponseDto(userInfoDto, purchasesResponseDtoList, memoStr);

        senderClient.sendEvent("roomInfo", roomInfoResponseDto);
        log.info("room관련 정보 발송" + roomInfoResponseDto.getUserInfo().getNickname());
    }

    public void getRoomList(SocketIOClient senderClient, String username) {
        //진행중은 아래의 피어 확인으로 찾고, 대기는 memo가 생기는 걸로 찾을까, 그럼 대기 말고 완료는 관리자가 나간걸로 찾을까(한달)
        // 대기중
        List<RoomResponseDto> waitingList = roomService.getRoomListContainUsername(username, 0);
        // 진행중
        List<RoomResponseDto> progressList = roomService.getRoomListContainUsername(username, 1);
        // 완료
        List<RoomResponseDto> doneList = roomService.getRoomListContainUsername(username, 2);

        Integer totalCount = waitingList.size() + progressList.size() + doneList.size();

        RoomListResponseDto roomListResponseDto = new RoomListResponseDto(waitingList, progressList, doneList, totalCount);

        senderClient.sendEvent("connected", roomListResponseDto);
        log.info("roomList 발송");
    }

    public void checkRoom(String room) {
        log.info("room 존재 여부 확인");
        if (!roomService.isRoom(room)) {
            log.info("새로운 방 생성");
            roomService.createRoom(room);
            // 빈 메모 등록
            String memoText = "";
            Memo memo = new Memo(room, memoText);
            memoService.saveMemo(memo);
        }
//        log.info("peer 다시 등록");
//        roomService.rejoinRoom(room);
    }

//    public void leaveRoom(String room, String username) {
//        // 전체적으로 room유효검사 부분을 추가해야할듯 하다.
//        log.info("방 나가기 처리중");
//        roomService.leaveRoom(room, username);
//    }

    public Message saveLeaveMessage(String room, String username) {
        username = username.equals("E001") ? "관리자" : username;
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

    private List<SocketIOClient> allClientInRoomWithOutSelf(SocketIOClient senderClient, String room) {
        return senderClient.getNamespace()
                .getRoomOperations(room)
                .getClients()
                .stream()
                .filter(client -> !client.getSessionId().equals(senderClient.getSessionId()))
                .toList();
    }

    private BroadcastOperations allClientInRoom(SocketIOClient senderClient, String room) {
        return senderClient.getNamespace()
                .getRoomOperations(room);
    }

    public void joinedRTC(SocketIOClient senderClient, Message message) {
        for (SocketIOClient client : allClientInRoomWithOutSelf(senderClient, message.getRoom())) {
            log.info("joinedRTC발송");
            client.sendEvent("joinedRTC", "상대방이 접속했습니다.");
        }
    }

    @Transactional
    public void saveMemo(Message message) {
        if (!message.getUsername().equals("E001")) {
            return;
        }
        String room = message.getRoom();
        String memoText = message.getContent();
        Memo memo = memoService.getMemo(room).orElseThrow(() ->
                new NullPointerException("memo가 존재하지 않습니다.")
        );
        memo.update(memoText);
    }

    public void joinAdmin(String room, String username) {
        if (!username.equals("E001")) {
            return;
        }
        roomService.joinAdmin(room);
    }

    public void leaveAdmin(String room, String username) {
        if (!username.equals("E001")) {
            return;
        }
        roomService.leaveAdmin(room);
    }

}
