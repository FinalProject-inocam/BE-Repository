package com.example.finalproject.domain.shop.controller;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.auth.security.UserDetailsImpl;
import com.example.finalproject.domain.shop.dto.ReviewRequestDto;
import com.example.finalproject.domain.shop.service.ReviewService;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.responsedto.ApiResponse;
import com.example.finalproject.global.utils.ResponseUtils;
import com.example.finalproject.global.utils.ValidationSequence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/shops/{shopId}/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping()
    public ApiResponse<?> createReview(@PathVariable String shopId,
                                       @RequestPart(value = "images", required = false) List<MultipartFile> multipartFile,
                                       @RequestPart(value = "data") @Validated(ValidationSequence.class) ReviewRequestDto requestDto,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        log.info(user.getEmail());
        SuccessCode successCode = reviewService.createReview(shopId, multipartFile, requestDto, user);
        return ResponseUtils.ok(successCode);
    }

    @PatchMapping("{reviewId}")
    public ApiResponse<?> updateReview(@PathVariable String shopId,
                                       @PathVariable Long reviewId,
                                       @RequestPart(value = "images", required = false) List<MultipartFile> multipartFile,
                                       @RequestPart(value = "data") @Validated(ValidationSequence.class) ReviewRequestDto requestDto,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        SuccessCode successCode = reviewService.updateReview(shopId, multipartFile, reviewId, requestDto, user);
        return ResponseUtils.ok(successCode);
    }

    @DeleteMapping("{reviewId}")
    public ApiResponse<?> deleteReview(@PathVariable String shopId,
                                       @PathVariable Long reviewId,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        SuccessCode successCode = reviewService.deleteReview(shopId, reviewId, user);
        return ResponseUtils.ok(successCode);
    }
}
