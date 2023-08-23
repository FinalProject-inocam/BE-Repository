package com.example.finalproject.domain.sockettest.dto;

import com.example.finalproject.domain.sockettest.model.Room;
import lombok.Getter;

@Getter
public class RoomResponseDto {
    private String room;
    private String peer;

    public RoomResponseDto(Room room, String username) {
        this.room = room.getRoomName();
        String usernameStart = username + "!";
        String usernameEnd = "!" + username;
        this.peer = room.getRoomName()
                .replaceAll(usernameStart, "")
                .replaceAll(usernameEnd, "");
    }
}
