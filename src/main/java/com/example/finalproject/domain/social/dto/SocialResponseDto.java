package com.example.finalproject.domain.social.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SocialResponseDto {
    private String email;
    private String nickname;

    public SocialResponseDto(String email,String nickname) {
        this.email=email;
        this.nickname=nickname;
    }
}