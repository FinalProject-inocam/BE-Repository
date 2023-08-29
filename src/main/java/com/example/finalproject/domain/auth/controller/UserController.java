package com.example.finalproject.domain.auth.controller;

import com.example.finalproject.domain.auth.dto.SignupRequestDto;
import com.example.finalproject.domain.auth.service.GoogleService;
import com.example.finalproject.domain.auth.service.KakaoService;
import com.example.finalproject.domain.auth.service.NaverService;
import com.example.finalproject.domain.auth.service.UserService;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.responsedto.ApiResponse;
import com.example.finalproject.global.utils.ResponseUtils;
import com.example.finalproject.global.validation.ValidationSequence;
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
    private final NaverService naverService;

    //구글 로그인
    @GetMapping("/login/google")
    public ApiResponse<?> googleLogin(@RequestParam String code, HttpServletResponse response) throws IOException, ServletException {
        SuccessCode successCode = googleService.googleLogin(code, response);
        return ResponseUtils.ok(successCode);
    }

    // 카카오 로그인
    @GetMapping("/login/kakao")
    // GET https://kauth.kakao.com/oauth/authorize?client_id={client_id}&response_type=code&redirect_uri={redirect_uri}
    public ApiResponse<?> kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        return kakaoService.kakaoLogin(code, response);
    }

    // 네이버 로그인
    @GetMapping("/login/naver")
    // GET https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id={client_id}&state=STATE_STRING&redirect_uri={redirect_uri}
    public ApiResponse<?> naverLogin(@RequestParam String code, @RequestParam String state, HttpServletResponse response) throws JsonProcessingException {
        return naverService.naverLogin(code, state, response);
    }

    @PostMapping("/signup")
    public ApiResponse<?> signup(@RequestBody @Validated(ValidationSequence.class) SignupRequestDto requestDto) {
        SuccessCode successCode = userService.signup(requestDto);
        return ResponseUtils.ok(successCode);
    }

    @GetMapping("/email")
    public ApiResponse<?> checkEmail(@RequestParam String email) {
        SuccessCode successCode = userService.checkEmail(email);
        return ResponseUtils.ok(successCode);
    }

    @GetMapping("/nickname")
    public ApiResponse<?> checkNickname(@RequestParam String nickname) {
        SuccessCode successCode = userService.checkNickname(nickname);
        return ResponseUtils.ok(successCode);
    }

    @GetMapping("/logout")
    public ApiResponse<?> logout(HttpServletRequest request) {
        SuccessCode successCode = userService.logout(request);
        return ResponseUtils.ok(successCode);
    }
}