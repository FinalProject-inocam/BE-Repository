package com.example.finalproject.domain.auth.dto;

import com.example.finalproject.global.validation.ValidationGroups.NotBlankGroup;
import com.example.finalproject.global.validation.ValidationGroups.PatternGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class SignupRequestDto {

    @Pattern(regexp = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$", message = "이메일 형식이 맞는지 확인해 주세요", groups = PatternGroup.class)
    @NotBlank(message = "이메일은 필수입니다.", groups = NotBlankGroup.class)
    private String email;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#?$%^&*]).{8,15}$", message = "비밀번호는 영문자와 숫자, 특수문자를 포함한 8~15자 이내로 작성해주세요", groups = PatternGroup.class)
    @NotBlank(message = "패스워드는 필수입니다.", groups = NotBlankGroup.class)
    private String password;

//    @Pattern(regexp = "^(?!date$|server$)(?!.*!)$", message = "사용 불가능한 단어 혹은 특수문자가 포함되어 있습니다.", groups = PatternGroup.class)
    @NotBlank(message = "닉네임은 필수입니다.", groups = NotBlankGroup.class)
    private String nickname;

    private String gender = null;
    private Integer birthYear = null;
    private String phoneNumber = null;
    private boolean admin = false;
    private String adminToken = "";
}
