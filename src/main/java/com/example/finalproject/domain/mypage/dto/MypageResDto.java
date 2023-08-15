package com.example.finalproject.domain.mypage.dto;

import com.example.finalproject.domain.auth.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MypageResDto {
    private String profileImg;
    private String nickname;
    private String phoneNumber;

    public MypageResDto(User user) {
        this.profileImg = user.getProfileImg();
        this.nickname = user.getNickname();
        this.phoneNumber = user.getPhoneNumber();
    }
}