package com.example.finalproject.domain.sockettest.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OfferAndAnswerDto {
    private String type;
    private String sdp;
    // room
    private String room;

    @Builder
    public OfferAndAnswerDto(String type, String sdp, String room) {
        this.type = type;
        this.sdp = sdp;
        // room
        this.room = room;
    }
}