package com.example.finalproject.domain.sockettest.service;

import com.example.finalproject.domain.sockettest.dto.RoomResponseDto;
import com.example.finalproject.domain.sockettest.model.Room;
import com.example.finalproject.domain.sockettest.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public void createRoom(String roomName) {
        Room room = new Room(roomName);
        // 실존하는 username과 채팅을 하려는게 맞는지 확인
        roomRepository.save(room);
    }

    public List<RoomResponseDto> getRoomListContainUsername(String username) {
        // 생각해본결과 username에 현재 특수 문자 !를 사용가능하여 문제가 있을것 같음 해결책 1 특수문자 사용 금지하기, 2 username이 아닌 id로 방이름 만들기
        String usernameStart = username + "!";
        String usernameEnd = "!" + username;
        List<Room> roomList = roomRepository.findAllByPeer1OrPeer2(username, username);
//        List<Room> roomList = roomRepository.findAllByRoomNameStartsWithOrRoomNameEndsWith(usernameStart, usernameEnd);
        List<RoomResponseDto> responseDtos = roomList.stream()
                .map((room) -> new RoomResponseDto(room, username))
                .toList();
        return responseDtos;
    }

    public Boolean isRoom(String roomName) {
        try {
            validateRoom(roomName);
            return true;
        } catch (NullPointerException e) {
            return false;
        }
    }

    @Transactional
    public void leaveRoom(String roomName, String username) {
        Room room = validateRoom(roomName);
        room.leavePeer(username);
    }

    private Room validateRoom(String roomName) {
        Room room = roomRepository.findByRoomName(roomName).orElseThrow(() ->
                new NullPointerException("존재하지 않는 후기입니다.")
        );
        return room;
    }


}
