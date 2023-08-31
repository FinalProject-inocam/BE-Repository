package com.example.finalproject.domain.mypage.dto;

import com.example.finalproject.global.validation.ValidationGroups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MypageRequestDto {
    @NotBlank(message = "닉네임은 필수입니다.", groups = ValidationGroups.NotBlankGroup.class)
    String nickname;

    String phoneNumber;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#?$%^&*]).{8,15}$", message = "영문자와 숫자, 특수문자를 포함한 8~15자 이내로 작성해주세요")
    String password;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#?$%^&*]).{8,15}$", message = "영문자와 숫자, 특수문자를 포함한 8~15자 이내로 작성해주세요")
    String newPassword;

    public void setPasswordToNewPassword(String encode) { // 기본 setter와 같은 이름
        this.newPassword = encode;
    }
}
