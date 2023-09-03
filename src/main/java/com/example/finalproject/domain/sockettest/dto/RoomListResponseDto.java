package com.example.finalproject.domain.sockettest.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class RoomListResponseDto {
    private Integer totalCount;
    private List<RoomResponseDto> wait;
    private List<RoomResponseDto> progress;
    private List<RoomResponseDto> done;

    public RoomListResponseDto(List<RoomResponseDto> wait, List<RoomResponseDto> progress, List<RoomResponseDto> done, Integer totalCount) {
        this.totalCount = totalCount;
        this.wait = wait;
        this.progress = progress;
        this.done = done;
    }
}