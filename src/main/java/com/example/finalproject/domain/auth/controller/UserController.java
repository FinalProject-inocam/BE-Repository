package com.example.finalproject.domain.auth.controller;

import com.example.finalproject.domain.auth.dto.SignupRequestDto;
import com.example.finalproject.domain.auth.service.UserService;
import com.example.finalproject.global.responsedto.ApiResponse;
import com.example.finalproject.global.utils.ResponseUtils;
import com.example.finalproject.global.utils.ValidationSequence;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

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