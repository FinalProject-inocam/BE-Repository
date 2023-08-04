package com.example.finalproject.domain.auth.controller;

import com.example.finalproject.domain.auth.dto.SignupRequestDto;
import com.example.finalproject.domain.auth.service.KakaoService;
import com.example.finalproject.domain.auth.service.UserService;
import com.example.finalproject.global.responsedto.ApiResponse;
import com.example.finalproject.global.utils.ResponseUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;
    private final KakaoService kakaoService;

    // 카카오 로그인
    @GetMapping("/auth/kakao")
    // GET https://kauth.kakao.com/oauth/authorize?client_id=ca694ae46e22b997351afa5a92c6c63a&response_type=code&redirect_uri=http://localhost:3000/api/auth/kakao
    public ApiResponse<?> kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        return kakaoService.kakaoLogin(code, response);
    }

    @PostMapping("/signup")
    public ApiResponse<?> signup(@RequestBody @Valid SignupRequestDto requestDto) {
        return ResponseUtils.ok(userService.signup(requestDto));
    }

    @GetMapping("/email")
    public ApiResponse<?> checkEmail(@RequestParam String email) {
        return ResponseUtils.ok(!userService.checkEmail(email));
    }

    @GetMapping("/nickname")
    public ApiResponse<?> checkNickname(@RequestParam String nickname) {
        return ResponseUtils.ok(!userService.checkNickname(nickname));
    }

    @GetMapping("/logout")
    public ApiResponse<?> logout(HttpServletRequest request) {
        return ResponseUtils.ok(userService.logout(request));
    }

}