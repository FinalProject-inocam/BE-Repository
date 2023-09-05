package com.example.finalproject.domain.sockettest.dto;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.global.enums.UserGenderEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserInfoDto {
    private String email;
    private String nickname;
    private String phoneNumber;
    private String gender;
    private Integer birthYear;

    public UserInfoDto(User user) {
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.phoneNumber = user.getPhoneNumber() != null ? user.getPhoneNumber() : "";
        this.gender = user.getGender() != null ? String.valueOf(user.getGender()) : "";
        this.birthYear = user.getBirthYear() != null ? user.getBirthYear() : 0;
    }
}