package com.example.finalproject.domain.sockettest.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AnswerRoomDto {
    private OfferAndAnswerDto answer;
    private String room;

    @Builder
    public AnswerRoomDto(OfferAndAnswerDto offerAndAnswerDto, String room) {
        this.answer = offerAndAnswerDto;
        this.room = room;
    }
}