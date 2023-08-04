package com.example.finalproject.domain.purchases.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MypageRequestDto {
    String nickname;
    String phoneNumber;
    String password;
    String newPassword;

    public void setPasswordToNewPassword(String encode) { // 기본 setter와 같은 이름
        this.newPassword = encode;
    }
}
