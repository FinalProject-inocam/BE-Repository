package com.example.finalproject.domain.purchases.controller;


import com.example.finalproject.auth.security.UserDetailsImpl;
import com.example.finalproject.domain.purchases.dto.PurchasesRequestDto;
import com.example.finalproject.domain.purchases.service.PurchasesService;
import com.example.finalproject.global.responsedto.ApiResponse;
import com.example.finalproject.global.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor

public class PurchasesController {

    private final PurchasesService purchasesService;

    // 차량 신청 내역 조회 (마이페이지)
    @GetMapping
    public ApiResponse<?> getPurchases(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return purchasesService.findAllPurchases(userDetails.getUser());
    }

    // 차량 출고 신청
    @PostMapping
    public ApiResponse<?> createPurchases(@RequestBody PurchasesRequestDto purchasesRequestDto,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return purchasesService.createPurchases(purchasesRequestDto, userDetails.getUser());
    }

    // 차량 신청 내역 수정
    @PatchMapping("/{purchaseId}")
    public ApiResponse<?> updatePurchases(@PathVariable Long purchaseId,
                                          @RequestBody PurchasesRequestDto purchasesRequestDto,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseUtils.ok(purchasesService.updatePurchases(purchaseId, purchasesRequestDto, userDetails.getUser()));
    }

    // 차량 신청 취소
    @DeleteMapping("/{purchaseId}")
    public ApiResponse<?> deletePurchases(@PathVariable Long purchaseId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseUtils.ok(purchasesService.deletePurchases(purchaseId, userDetails.getUser()));
    }

}
