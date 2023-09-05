package com.example.finalproject.domain.sockettest.dto;

import com.example.finalproject.domain.sockettest.entity.Room;
import lombok.Getter;

@Getter
public class RoomResponseDto {
    private String room;
    private String peer;
    private String lastMessage;

    public RoomResponseDto(Room room, String username, String lastMessage) {
        this.room = room.getRoomName();
        String usernameStart = username + "!";
        String usernameEnd = "!" + username;
        this.peer = room.getRoomName()
                .replaceAll(usernameStart, "")
                .replaceAll(usernameEnd, "");
        this.lastMessage = lastMessage;
    }
}
