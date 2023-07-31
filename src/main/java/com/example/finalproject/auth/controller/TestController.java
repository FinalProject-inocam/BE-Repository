package com.example.finalproject.auth.controller;

import com.example.finalproject.auth.service.UserService;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.responsedto.ApiResponse;
import com.example.finalproject.global.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TestController {

    private final UserService userService;

    @GetMapping("/test")
    public ApiResponse<?> test() {
        return ResponseUtils.ok(SuccessCode.LIKE_SUCCESS);
    }
}
