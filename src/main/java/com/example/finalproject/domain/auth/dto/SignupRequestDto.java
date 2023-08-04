package com.example.finalproject.domain.auth.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class SignupRequestDto {

    @Pattern(regexp = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$", message = "이메일 형식에 맞춰주세요")
    @NotBlank
    private String email;

    // 테스트 중에는 꺼두겠습니다...
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#?$%^&*]).{8,15}$", message = "영문자와 숫자, 특수문자를 포함한 8~15자 이내로 작성해주세요")
    @NotBlank
    private String password;

    @NotBlank
    private String nickname;

    @NotBlank
    private String gender;

    @NotBlank
    private String birthdate;

    @NotBlank
    private String phoneNumber;

    private boolean admin = false;

    private String adminToken = "";
}
