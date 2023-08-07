package com.example.finalproject.domain.purchases.controller;

import com.example.finalproject.domain.auth.security.UserDetailsImpl;
import com.example.finalproject.domain.purchases.dto.MypageRequestDto;
import com.example.finalproject.domain.purchases.service.MypageService;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.responsedto.ApiResponse;
import com.example.finalproject.global.utils.ResponseUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MypageController {
    private final MypageService mypageService;

    // 마이페이지 수정
    @PatchMapping
    public ApiResponse<?> updateMypage(@RequestPart(value = "images", required = false) MultipartFile multipartFile,
                                       @RequestPart(value = "data") MypageRequestDto mypageRequestDto,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails,
                                       HttpServletResponse response) {
        SuccessCode successCode = mypageService.updateMypage(multipartFile, mypageRequestDto, userDetails.getUser(), response);
        return ResponseUtils.ok(successCode);
    }
}
