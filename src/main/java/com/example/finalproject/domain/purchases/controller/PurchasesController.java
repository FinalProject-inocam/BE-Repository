package com.example.finalproject.domain.purchases.controller;


import com.example.finalproject.domain.auth.security.UserDetailsImpl;
import com.example.finalproject.domain.purchases.dto.PurchasesPatchResponseDto;
import com.example.finalproject.domain.purchases.dto.PurchasesRequestDto;
import com.example.finalproject.domain.purchases.dto.PurchasesResponseDto;
import com.example.finalproject.domain.purchases.service.PurchasesService;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.responsedto.ApiResponse;
import com.example.finalproject.global.utils.ResponseUtils;
import com.example.finalproject.global.validation.ValidationSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor

public class PurchasesController {

    private final PurchasesService purchasesService;

    // 차량 신청 내역 조회 (마이페이지)
    @GetMapping
    public Page<PurchasesResponseDto> getPurchases(@RequestParam("page") int page,
                                                   @RequestParam("size") int size,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return purchasesService.findAllPurchases(page, size, userDetails.getUser());
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
                                          @RequestBody @Validated(ValidationSequence.class) PurchasesRequestDto purchasesRequestDto,
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
