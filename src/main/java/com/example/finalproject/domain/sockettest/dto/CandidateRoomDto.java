package com.example.finalproject.domain.sockettest.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CandidateRoomDto {
    private CandidateDto candidate;
    private String room;

    @Builder
    public CandidateRoomDto(CandidateDto candidateDto, String room) {
        this.candidate = candidateDto;
        this.room = room;

    }
}