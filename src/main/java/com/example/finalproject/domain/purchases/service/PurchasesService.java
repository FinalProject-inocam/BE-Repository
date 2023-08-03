package com.example.finalproject.domain.purchases.service;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.post.exception.PostsNotFoundException;
import com.example.finalproject.domain.purchases.dto.PurchasesRequestDto;
import com.example.finalproject.domain.purchases.dto.PurchasesResponseDto;
import com.example.finalproject.domain.purchases.entity.Purchase;
import com.example.finalproject.domain.purchases.repository.PurchasesRepository;
import com.example.finalproject.global.responsedto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.finalproject.global.enums.ErrorCode.NOT_FOUND_DATA;
import static com.example.finalproject.global.enums.ErrorCode.NO_AUTHORITY_TO_DATA;
import static com.example.finalproject.global.enums.SuccessCode.POST_CREATE_SUCCESS;
import static com.example.finalproject.global.enums.SuccessCode.POST_DELETE_SUCCESS;
import static com.example.finalproject.global.utils.ResponseUtils.ok;

@Service
@RequiredArgsConstructor
public class PurchasesService {
    private final PurchasesRepository purchasesRepository;

    // 차량 신청 내역 조회 (마이페이지)
    public ApiResponse<?> findAllPurchases(User user) {
        List<PurchasesResponseDto> purchasesList = purchasesRepository.findAllByUser(user)
                .stream()
                .map(PurchasesResponseDto::new)
                .collect(Collectors.toList());

        return ok(purchasesList);
    }

    // 차량 출고 신청
    public ApiResponse<?> createPurchases(PurchasesRequestDto purchasesRequestDto, User user) {
        Purchase purchase = new Purchase(purchasesRequestDto, user);
        purchasesRepository.save(purchase);
        return ok(POST_CREATE_SUCCESS);
    }


    // 차량 신청 내역 수정
    public PurchasesResponseDto updatePurchases(Long purchaseId, PurchasesRequestDto purchasesRequestDto, User user) {
        Purchase purchase = findPurchases(purchaseId);
        checkUsername(purchaseId, user);
        purchase.update(purchasesRequestDto);
        return new PurchasesResponseDto(purchase);
    }

    // 차량 신청 취소
    public ApiResponse<?> deletePurchases(Long purchaseId, User user) {
        Purchase purchase = findPurchases(purchaseId);
        checkUsername(purchaseId, user);
        purchasesRepository.delete(purchase);
        return ok(POST_DELETE_SUCCESS);
    }

    private Purchase findPurchases(Long purchaseId) {
        return purchasesRepository.findById(purchaseId).orElseThrow(() ->
                new PostsNotFoundException(NOT_FOUND_DATA)
        );
    }

    private void checkUsername(Long purchaseId, User user) {
        Purchase purchase = findPurchases(purchaseId);
        if (!(purchase.getUser().getUserId().equals(user.getUserId()))) {
            throw new PostsNotFoundException(NO_AUTHORITY_TO_DATA);
        }
    }

}
