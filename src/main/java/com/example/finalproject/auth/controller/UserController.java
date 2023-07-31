package com.example.finalproject.auth.controller;

import com.example.finalproject.auth.dto.SignupRequestDto;
import com.example.finalproject.auth.service.UserService;
import com.example.finalproject.global.responsedto.ApiResponse;
import com.example.finalproject.global.utils.ResponseUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ApiResponse<?> signup(@RequestBody @Valid SignupRequestDto requestDto) {
        return ResponseUtils.ok(userService.signup(requestDto));
    }

    @PostMapping("/email")
    public ApiResponse<?> checkEmail(@RequestParam String email) {
        return ResponseUtils.ok(userService.checkEmail(email));
    }

    @PostMapping("/nickname")
    public ApiResponse<?> checkNickname(@RequestParam String nickname) {
        return ResponseUtils.ok(userService.checkNickname(nickname));
    }

    @PostMapping("/logout")
    public ApiResponse<?> logout(HttpServletRequest request) {
        return ResponseUtils.ok(userService.logout(request));
    }

}