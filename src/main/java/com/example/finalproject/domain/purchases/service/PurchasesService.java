package com.example.finalproject.domain.purchases.service;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.car.entity.Car;
import com.example.finalproject.domain.car.repository.CarRepository;
import com.example.finalproject.domain.post.exception.PostsNotFoundException;
import com.example.finalproject.domain.purchases.dto.request.PurchasesRequestDto;
import com.example.finalproject.domain.purchases.dto.response.PurchasesPatchResponseDto;
import com.example.finalproject.domain.purchases.dto.response.PurchasesResponseDto;
import com.example.finalproject.domain.purchases.entity.Purchase;
import com.example.finalproject.domain.purchases.exception.PurchasesNotFoundException;
import com.example.finalproject.domain.purchases.repository.PurchasesRepository;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.responsedto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final CarRepository carRepository;

    // 차량 신청 내역 조회 (마이페이지)
    public Page<PurchasesResponseDto> findAllPurchases(int page, int size, User user) {
        // 사용자별 구매 목록 조회
        purchasesRepository.findAllByUser(user);

        // 페이지 나누기
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "purchaseId"));
        Page<Purchase> purchasesPage = purchasesRepository.findAll(pageable);

        List<PurchasesResponseDto> purchasesResponseDtoList = new ArrayList<>();

        for (Purchase purchase : purchasesPage) {
            PurchasesResponseDto purchasesResponseDto = new PurchasesResponseDto(purchase);
            purchasesResponseDtoList.add(purchasesResponseDto);
        }
        return new PageResponse<>(purchasesResponseDtoList, pageable, purchasesPage.getTotalElements());
    }

    // 차량 출고 신청
    public SuccessCode createPurchases(PurchasesRequestDto purchasesRequestDto, User user) {
        Car car = carRepository.findByType(purchasesRequestDto.getType());
        Purchase purchase = new Purchase(purchasesRequestDto, user, car.getPrice());
        purchasesRepository.save(purchase);
        return PURCHASES_CREATE_SUCCESS;
    }

    // 차량 신청 내역 수정
    @Transactional
    public PurchasesPatchResponseDto updatePurchases(Long purchaseId, PurchasesRequestDto purchasesRequestDto, User user) {
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
