package com.example.finalproject.domain.purchases.controller;


import com.example.finalproject.domain.auth.security.UserDetailsImpl;
import com.example.finalproject.domain.purchases.dto.request.PurchasesRequestDto;
import com.example.finalproject.domain.purchases.dto.request.PurchasesUpdateRequestDto;
import com.example.finalproject.domain.purchases.dto.response.PurchasesPatchResponseDto;
import com.example.finalproject.domain.purchases.dto.response.PurchasesResponseDto;
import com.example.finalproject.domain.purchases.service.PurchasesService;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.responsedto.ApiResponse;
import com.example.finalproject.global.utils.ResponseUtils;
import com.example.finalproject.global.validation.ValidationSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor

public class PurchasesController {

    private final PurchasesService purchasesService;

    @GetMapping
    public ApiResponse<?> getPurchases(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<PurchasesResponseDto> purchasesResponseDtoList = purchasesService.findAllPurchases(userDetails.getUser());
        return ResponseUtils.ok(purchasesResponseDtoList);
    }

    // 차량 출고 신청
    @PostMapping
    public ApiResponse<?> createPurchases(@RequestBody @Validated(ValidationSequence.class) PurchasesRequestDto purchasesRequestDto,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        SuccessCode successCode = purchasesService.createPurchases(purchasesRequestDto, userDetails.getUser());
        return ResponseUtils.ok(successCode);
    }

    // 차량 신청 내역 수정
    @PatchMapping("/{purchaseId}")
    public ApiResponse<?> updatePurchases(@PathVariable Long purchaseId,
                                          @RequestBody @Validated(ValidationSequence.class) PurchasesUpdateRequestDto purchasesRequestDto,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        PurchasesPatchResponseDto purchasesPatchResponseDto = purchasesService.updatePurchases(purchaseId, purchasesRequestDto, userDetails.getUser());
        return ResponseUtils.ok(purchasesPatchResponseDto);
    }

    // 차량 신청 취소
    @DeleteMapping("/{purchaseId}")
    public ApiResponse<?> deletePurchases(@PathVariable Long purchaseId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        SuccessCode successCode = purchasesService.deletePurchases(purchaseId, userDetails.getUser());
        return ResponseUtils.ok(successCode);
    }
}
