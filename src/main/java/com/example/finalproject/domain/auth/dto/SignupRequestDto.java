package com.example.finalproject.domain.auth.dto;

import com.example.finalproject.global.utils.ValidationGroups.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class SignupRequestDto {

    @Pattern(regexp = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$", message = "이메일 형식이 맞는지 확인해 주세요", groups = PatternGroup.class)
    @NotBlank(message = "이메일은 필수입니다.", groups = NotBlankGroup.class)
    private String email;

    // 테스트 중에는 꺼두겠습니다...
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#?$%^&*]).{8,15}$", message = "비밀번호는 영문자와 숫자, 특수문자를 포함한 8~15자 이내로 작성해주세요", groups = PatternGroup.class)
    @NotBlank(message = "패스워드는 필수입니다.", groups = NotBlankGroup.class)
    private String password;

    @NotBlank(message = "닉네임은 필수입니다.", groups = NotBlankGroup.class)
    private String nickname;

    @NotBlank(message = "성별은 필수입니다.", groups = NotBlankGroup.class)
    private String gender;

    @NotBlank(message = "생년월일은 필수입니다.", groups = NotBlankGroup.class)
    private String birthdate;

    @NotBlank(message = "휴대전화번호는 필수입니다.", groups = NotBlankGroup.class)
    private String phoneNumber;

    private boolean admin = false;

    private String adminToken = "";
}
