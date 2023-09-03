package com.example.finalproject.domain.sockettest.service;

import com.example.finalproject.domain.sockettest.dto.RoomResponseDto;
import com.example.finalproject.domain.sockettest.entity.Message;
import com.example.finalproject.domain.sockettest.entity.Room;
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
    private final MessageService messageService;

    public void createRoom(String roomName) {
        Room room = new Room(roomName);
        // 실존하는 username과 채팅을 하려는게 맞는지 확인
        roomRepository.save(room);
    }

    public List<RoomResponseDto> getRoomListContainUsername(String username, Integer status) {
        List<Room> roomList = roomRepository.findAllByRoomNameContainingAndStatus(username, status);
        List<RoomResponseDto> responseDtos = roomList.stream()
                .map((room) -> new RoomResponseDto(room, username, lastMessage(room)))
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

    @Transactional
    public void rejoinRoom(String roomName) {
        Room room = validateRoom(roomName);

        room.rejoinPeer(roomName);
    }

    private Room validateRoom(String roomName) {
        Room room = roomRepository.findByRoomName(roomName).orElseThrow(() ->
                new NullPointerException("존재하지 않는 방입니다.")
        );
        return room;
    }

    @Transactional
    public void joinAdmin(String roomName) {
        Room room = validateRoom(roomName);
        room.joinAdmin();
    }

    @Transactional
    public void leaveAdmin(String roomName) {
        Room room = validateRoom(roomName);
        room.leaveAdmin();
    }

    private String lastMessage(Room room) {
        Message message = messageService.getLastMessage(room.getRoomName());
        if (message == null) {
            return "";
        }
        return message.getContent();
    }

}
