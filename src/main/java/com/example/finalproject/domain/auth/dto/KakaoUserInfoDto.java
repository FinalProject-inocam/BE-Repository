package com.example.finalproject.domain.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoUserInfoDto {
    private Long id;
    private String nickname;
    private String email;
    private String gender;

    public KakaoUserInfoDto(Long id, String nickname, String email, String gender) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.gender = gender;
//        this.birthday = birthday;
    }
}