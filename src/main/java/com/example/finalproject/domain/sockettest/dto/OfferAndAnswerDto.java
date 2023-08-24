package com.example.finalproject.domain.sockettest.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OfferAndAnswerDto {
    private String type;
    private String sdp;


    @Builder
    public OfferAndAnswerDto(String type, String sdp) {
        this.type = type;
        this.sdp = sdp;
    }
}