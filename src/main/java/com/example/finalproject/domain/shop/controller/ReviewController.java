package com.example.finalproject.domain.shop.controller;

import com.example.finalproject.domain.auth.security.UserDetailsImpl;
import com.example.finalproject.domain.shop.dto.request.ReviewRequestDto;
import com.example.finalproject.domain.shop.dto.response.ReviewStarResponseDto;
import com.example.finalproject.domain.shop.dto.response.ReviewpageResponseDto;
import com.example.finalproject.domain.shop.service.ReviewService;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.responsedto.ApiResponse;
import com.example.finalproject.global.utils.ResponseUtils;
import com.example.finalproject.global.validation.ValidationSequence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Slf4j(topic = "ReviewController")
@RequiredArgsConstructor
@RequestMapping("/api/shops/{shopId}/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping
    public ApiResponse<?> reviewList(@RequestParam("page") int page,
                                     @RequestParam("size") int size,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails,
                                     @PathVariable String shopId) {
        ReviewpageResponseDto reviewpageResponseDto = reviewService.reviewList(size, page, userDetails, shopId);
        return ResponseUtils.ok(reviewpageResponseDto);
    }

    @PostMapping
    public ApiResponse<?> createReview(@PathVariable String shopId,
                                       @RequestPart(value = "images", required = false) List<MultipartFile> multipartFile,
                                       @RequestPart(value = "data") @Validated(ValidationSequence.class) ReviewRequestDto requestDto,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
        SuccessCode successCode = reviewService.createReview(shopId, multipartFile, requestDto, userDetails);
        return ResponseUtils.ok(successCode);
    }

    @PatchMapping("/{reviewId}")
    public ApiResponse<?> updateReview(@PathVariable String shopId,
                                       @PathVariable Long reviewId,
                                       @RequestPart(value = "images", required = false) List<MultipartFile> multipartFile,
                                       @RequestPart(value = "data") @Validated(ValidationSequence.class) ReviewRequestDto requestDto,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
        SuccessCode successCode = reviewService.updateReview(shopId, multipartFile, reviewId, requestDto, userDetails);
        return ResponseUtils.ok(successCode);
    }

    @DeleteMapping("/{reviewId}")
    public ApiResponse<?> deleteReview(@PathVariable String shopId,
                                       @PathVariable Long reviewId,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
        SuccessCode successCode = reviewService.deleteReview(shopId, reviewId, userDetails);
        return ResponseUtils.ok(successCode);
    }


    @GetMapping("/star")
    public ApiResponse<?> getstar(@PathVariable String shopId) {
        ReviewStarResponseDto reviewStarResponseDto = reviewService.getStar(shopId);
        return ResponseUtils.ok(reviewStarResponseDto);
    }

    @PatchMapping("/{reviewId}/like")
    public ApiResponse<?> getlike(@PathVariable String shopId,
                                  @PathVariable Long reviewId,
                                  @AuthenticationPrincipal UserDetailsImpl userDetails) {
        SuccessCode successCode = reviewService.getlike(shopId, reviewId, userDetails);
        return ResponseUtils.ok(successCode);
    }
}