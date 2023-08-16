package com.example.finalproject.domain.shop.dto;

import lombok.Getter;

@Getter
public class ReviewStarResponseDto {
    private Long zero;
    private Long one;
    private Long two;
    private Long three;
    private Long four;
    private Long five;
    public ReviewStarResponseDto(int[] countStar){
        this.zero=Long.valueOf(countStar[0]);
        this.one=Long.valueOf(countStar[1]);
        this.two=Long.valueOf(countStar[2]);
        this.three=Long.valueOf(countStar[3]);
        this.four=Long.valueOf(countStar[4]);
        this.five=Long.valueOf(countStar[5]);
    }
}
