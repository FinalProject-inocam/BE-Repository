package com.example.finalproject.domain.social.controller;

import com.example.finalproject.domain.social.dto.SocialResponseDto;
import com.example.finalproject.domain.social.service.SocialService;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.responsedto.ApiResponse;
import com.example.finalproject.global.utils.ResponseUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/login/oauth2", produces = "application/json")
public class SocialController {

    private final SocialService socialService;


    @GetMapping("/code/{registrationId}")
    public ApiResponse<?> googleLogin(@RequestParam String code, @PathVariable String registrationId, HttpServletResponse response)throws IOException, ServletException {
        SuccessCode successCode =socialService.socialLogin(code, registrationId,response);
        return ResponseUtils.ok(successCode);
    }
}
