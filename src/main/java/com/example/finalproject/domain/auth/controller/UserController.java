package com.example.finalproject.domain.auth.controller;

import com.example.finalproject.domain.auth.dto.SignupRequestDto;
import com.example.finalproject.domain.auth.service.KakaoService;
import com.example.finalproject.domain.auth.service.UserService;
import com.example.finalproject.domain.auth.service.GoogleService;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.responsedto.ApiResponse;
import com.example.finalproject.global.utils.ResponseUtils;
import com.example.finalproject.global.utils.ValidationSequence;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;
    private final KakaoService kakaoService;
    private final GoogleService googleService;

    //구글 로그인
    @GetMapping("/login/google")
    public ApiResponse<?> googleLogin(@RequestParam String code, HttpServletResponse response)throws IOException, ServletException {
        SuccessCode successCode =googleService.googleLogin(code,response);
        return ResponseUtils.ok(successCode);
    }
    // 카카오 로그인
    @GetMapping("/kakao")
    // GET https://kauth.kakao.com/oauth/authorize?client_id=ca694ae46e22b997351afa5a92c6c63a&response_type=code&redirect_uri=http://localhost:3000/api/auth/kakao
    public ApiResponse<?> kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        return kakaoService.kakaoLogin(code, response);
    }

    @PostMapping("/signup")
    public ApiResponse<?> signup(@RequestBody @Validated(ValidationSequence.class) SignupRequestDto requestDto) {
        return ResponseUtils.ok(userService.signup(requestDto));
    }

    @GetMapping("/email")
    public ApiResponse<?> checkEmail(@RequestParam String email) {
        return ResponseUtils.ok(userService.checkEmail(email));
    }

    @GetMapping("/nickname")
    public ApiResponse<?> checkNickname(@RequestParam String nickname) {
        return ResponseUtils.ok(userService.checkNickname(nickname));
    }

    @GetMapping("/logout")
    public ApiResponse<?> logout(HttpServletRequest request) {
        return ResponseUtils.ok(userService.logout(request));
    }

}