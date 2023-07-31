package com.example.finalproject.domain.purchases.controller;


import com.example.finalproject.domain.purchases.dto.PurchasesRequestDto;
import com.example.finalproject.domain.purchases.service.PurchasesService;
import com.example.finalproject.global.responsedto.ApiResponse;
import com.example.finalproject.global.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor

public class PurchasesController {

    private final PurchasesService purchasesService;

    // 차량 신청 내역 조회
    @GetMapping
    public ApiResponse<?> getPurchases() {
        return purchasesService.findAllPurchases();
    }

    // 차량 출고 신청
    @PostMapping
    public ApiResponse<?> createPurchases(@RequestBody PurchasesRequestDto purchasesRequestDto) {
        return purchasesService.createPurchases(purchasesRequestDto);
    }

    // 차량 신청 내역 수정
    @PatchMapping("/{purchaseId}")
    public ApiResponse<?> updatePurchases(@PathVariable Long purchaseId,
                                          @RequestBody PurchasesRequestDto purchasesRequestDto) {
        return ResponseUtils.ok(purchasesService.updatePurchases(purchaseId, purchasesRequestDto));
    }

    // 차량 신청 취소
    @DeleteMapping("/{purchaseId}")
    public ApiResponse<?> deletePurchases(@PathVariable Long purchaseId) {
        return ResponseUtils.ok(purchasesService.deletePurchases(purchaseId));
    }

}
