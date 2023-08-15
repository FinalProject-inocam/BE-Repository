package com.example.finalproject.domain.admin.service;

import com.example.finalproject.domain.admin.dto.AllPurchasseResponseDto;
import com.example.finalproject.domain.admin.dto.ReleaseDecidereqDto;
import com.example.finalproject.domain.admin.dto.TotalListResponseDto;
import com.example.finalproject.domain.admin.exception.AdminNotFoundException;
import com.example.finalproject.domain.purchases.dto.PurchasesResponseDto;
import com.example.finalproject.domain.purchases.entity.Purchase;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.finalproject.global.enums.ErrorCode.NOT_FOUND_DATA;

@Service
@RequiredArgsConstructor
public class AdminListService {
    private final PurchasesRepository purchasesRepository;

    // 페이지 나누기
    @Transactional
    public Page<PurchasesResponseDto> allList(int page,int size){
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "purchaseId"));
        Page<Purchase> purchaseList=purchasesRepository.findAll(pageable);
        List<AllPurchasseResponseDto> purchasesResponseDtoList = new ArrayList<>();
        for (Purchase purchase : purchaseList) {
            AllPurchasseResponseDto allPurchasseResponseDto = new AllPurchasseResponseDto(purchase);
            purchasesResponseDtoList.add(allPurchasseResponseDto);
        }
        return new PageResponse(purchasesResponseDtoList, pageable, purchaseList.getTotalElements());
    }


    public TotalListResponseDto totalList() {
        LocalDateTime currentDateTime = LocalDateTime.now();

        int year = currentDateTime.getYear();
        int month = currentDateTime.getMonthValue();

        // 해당 월의 시작 및 끝 날짜 설정
        LocalDateTime startDateTime = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(year, month, startDateTime.toLocalDate().lengthOfMonth(), 23, 59, 59);

        Long needApprovalCount = purchasesRepository.countByApprove(null);
        Long approvedCount = purchasesRepository.countByApproveIsTrueAndCreatedAtBetween(startDateTime, endDateTime);
        Long deniedCount = purchasesRepository.countByApproveIsFalseAndCreatedAtBetween(startDateTime, endDateTime);

        System.out.println("출고 대기 : " + needApprovalCount);
        System.out.println("출고 승인 : " + approvedCount);
        System.out.println("출고 거절 : " + deniedCount);
        return new TotalListResponseDto(needApprovalCount, approvedCount, deniedCount);
    }

    @Transactional
    public SuccessCode releaseDecide(Long id, ReleaseDecidereqDto releaseDecidereqDto) {
        Purchase purchase = purchasesRepository.findById(id).orElseThrow(
                () -> new AdminNotFoundException(NOT_FOUND_DATA)
        );
        if (releaseDecidereqDto.getApprove()) {
            purchase.update(releaseDecidereqDto.getApprove(), releaseDecidereqDto.getDeliveryDate());
            return SuccessCode.PURCHASE_APPROVE;
        } else {
            purchase.update(releaseDecidereqDto.getApprove(), releaseDecidereqDto.getDenyMessage());
            return SuccessCode.PURCHASE_DENIED;
        }
    }

    public PurchasesResponseDto getone(Long purchaseId) {
        Purchase purchase=purchasesRepository.findById(purchaseId).orElseThrow(() ->
                new NullPointerException("존재하지않는 신청입니다.")
        );
        PurchasesResponseDto purchasesResponseDto=new PurchasesResponseDto(purchase);
        return purchasesResponseDto;
    }
}
