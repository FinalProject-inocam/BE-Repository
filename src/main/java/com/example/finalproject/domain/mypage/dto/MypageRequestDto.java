package com.example.finalproject.domain.mypage.dto;

import com.example.finalproject.global.validation.CustomConstraint.ProhibitValidation;
import com.example.finalproject.global.validation.ValidationGroups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MypageRequestDto {
    @ProhibitValidation(groups = ValidationGroups.ProhibitValidationGroup.class)
    @NotBlank(message = "닉네임은 필수입니다.", groups = ValidationGroups.NotBlankGroup.class)
    private String nickname;

    private String phoneNumber;
    private String password;
    private String newPassword;

    public void setPasswordToNewPassword(String encode) { // 기본 setter와 같은 이름
        this.newPassword = encode;
    }
}
