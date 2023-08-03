package com.example.finalproject.domain.purchases.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MypageRequestDto {
    String nickname;
    String phone_number;
    String password;
    String new_password;

    public void setPasswordToNewPassword(String encode) { // 기본 setter와 같은 이름
        this.new_password = encode;
    }
}
