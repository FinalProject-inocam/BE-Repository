package com.example.finalproject.domain.sockettest.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OfferRoomDto {
    private OfferAndAnswerDto offer;
    private String room;

    @Builder
    public OfferRoomDto(OfferAndAnswerDto offerAndAnswerDto, String room) {
        this.offer = offerAndAnswerDto;
        this.room = room;
    }
}