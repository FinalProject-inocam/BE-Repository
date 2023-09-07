package com.example.finalproject.domain.purchases.service;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.post.exception.PostsNotFoundException;
import com.example.finalproject.domain.purchases.dto.request.PurchasesRequestDto;
import com.example.finalproject.domain.purchases.dto.request.PurchasesUpdateRequestDto;
import com.example.finalproject.domain.purchases.dto.response.PurchasesPatchResponseDto;
import com.example.finalproject.domain.purchases.dto.response.PurchasesResponseDto;
import com.example.finalproject.domain.purchases.entity.Purchase;
import com.example.finalproject.domain.purchases.exception.PurchasesNotFoundException;
import com.example.finalproject.domain.purchases.repository.PurchasesRepository;
import com.example.finalproject.global.enums.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.example.finalproject.global.enums.ErrorCode.NO_AUTHORITY_TO_DATA;
import static com.example.finalproject.global.enums.ErrorCode.PURCHASES_DELETE_FAIL;
import static com.example.finalproject.global.enums.SuccessCode.PURCHASES_CREATE_SUCCESS;
import static com.example.finalproject.global.enums.SuccessCode.PURCHASES_DELETE_SUCCESS;

@Service
@RequiredArgsConstructor
public class PurchasesService {
    private final PurchasesRepository purchasesRepository;


    // '테스트 계정'만 차량 출고 신청 조회 최근 2건 조회하는 방법
    // 1. 디비에 유저아이디가 '2번'을 찾는다.  why? 2번은 테스트 계정이다.
    // 2. 조건을 돌린다. 만약에 2번이면 최근 2건을 찾고, 아니면 전체를 보여줘라.

    // 차량 신청 내역 조회 (마이페이지)
    public List<PurchasesResponseDto> findAllPurchases(User user) {
        List<Purchase> purchasesList;
        List<PurchasesResponseDto> purchasesResponseDtoList = new ArrayList<>();

        if ("minji11@test.com".equals(user.getEmail())) {
            purchasesList = purchasesRepository.findTop2ByUserOrderByCreatedAtDesc(user);
        } else {
            purchasesList = purchasesRepository.findAllByUserOrderByCreatedAtDesc(user);
        }
        for (Purchase purchase : purchasesList) {
            PurchasesResponseDto purchasesResponseDto = new PurchasesResponseDto(purchase);
            purchasesResponseDtoList.add(purchasesResponseDto);
        }
        return purchasesResponseDtoList;
    }

    // 차량 출고 신청
    public SuccessCode createPurchases(PurchasesRequestDto purchasesRequestDto, User user) {
        Purchase purchase = new Purchase(purchasesRequestDto, user);
        purchasesRepository.save(purchase);
        return PURCHASES_CREATE_SUCCESS;
    }

    // 차량 신청 내역 수정
    @Transactional
    public PurchasesPatchResponseDto updatePurchases(Long purchaseId, PurchasesUpdateRequestDto purchasesRequestDto, User user) {
        Purchase purchase = findPurchases(purchaseId);
        checkUsername(purchaseId, user);
        purchase.update(purchasesRequestDto);
        return new PurchasesPatchResponseDto(purchase);
    }

    // 차량 신청 취소
    public SuccessCode deletePurchases(Long purchaseId, User user) {
        Purchase purchase = findPurchases(purchaseId);
        checkUsername(purchaseId, user);
        purchasesRepository.delete(purchase);
        return PURCHASES_DELETE_SUCCESS;
    }

    private Purchase findPurchases(Long purchaseId) {
        return purchasesRepository.findById(purchaseId).orElseThrow(() ->
                new PurchasesNotFoundException(PURCHASES_DELETE_FAIL)
        );
    }

    private void checkUsername(Long purchaseId, User user) {
        Purchase purchase = findPurchases(purchaseId);
        if (!(purchase.getUser().getUserId().equals(user.getUserId()))) {
            throw new PostsNotFoundException(NO_AUTHORITY_TO_DATA);
        }
    }
}
