package com.example.finalproject.domain.admin.service;

import com.example.finalproject.domain.admin.dto.ReleaseDecidereqDto;
import com.example.finalproject.domain.admin.dto.TotalListResponseDto;
import com.example.finalproject.domain.admin.exception.AdminNotFoundException;
import com.example.finalproject.domain.post.exception.PostsNotFoundException;
import com.example.finalproject.domain.purchases.dto.PurchasesResponseDto;
import com.example.finalproject.domain.purchases.entity.Purchase;
import com.example.finalproject.domain.purchases.repository.PurchasesRepository;
import com.example.finalproject.global.enums.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.finalproject.global.enums.ErrorCode.NOT_FOUND_CLIENT;
import static com.example.finalproject.global.enums.ErrorCode.NOT_FOUND_DATA;

@Service
@RequiredArgsConstructor
public class AdminListService {
    private final PurchasesRepository purchasesRepository;

    public Page<PurchasesResponseDto> approveList(int page, int size) {

        // 페이지 나누기
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "purchaseId"));
        Page<Purchase> purchasesPage = purchasesRepository.findByApprove(pageable,null);

        List<PurchasesResponseDto> purchasesList = purchasesPage
                .stream()
                .map(PurchasesResponseDto::new)
                .collect(Collectors.toList());

        long total = purchasesPage.getTotalElements();

        return new PageImpl<>(purchasesList, pageable, total);
    }

    public Page<PurchasesResponseDto> decideList(int page, int size) {

        // 페이지 나누기
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "purchaseId"));
        Page<Purchase> purchasesPage = purchasesRepository.findByApprove(pageable,true);

        List<PurchasesResponseDto> purchasesList = purchasesPage
                .stream()
                .map(PurchasesResponseDto::new)
                .collect(Collectors.toList());

        long total = purchasesPage.getTotalElements();

        return new PageImpl<>(purchasesList, pageable, total);
    }

    public Page<PurchasesResponseDto> denyList(int page, int size) {

        // 페이지 나누기
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "purchaseId"));
        Page<Purchase> purchasesPage = purchasesRepository.findByApprove(pageable,false);

        List<PurchasesResponseDto> purchasesList = purchasesPage
                .stream()
                .map(PurchasesResponseDto::new)
                .collect(Collectors.toList());

        long total = purchasesPage.getTotalElements();

        return new PageImpl<>(purchasesList, pageable, total);
    }


//   신청건수 : 금달 + 이월건수
//	승인필요 총합 : approveList.length // 금달 + 이월건수
//	승인확정 총합 : decideList.length // 금달 + 이월건수
//	승인거절 총합 : denyList.length // 금달 + 이월건수
//   **** 딜레마 발생 ****
//   금달+이월건수인데 승인확정하고 승인거절에 이월건수라는게 존재할 수 있는가에 대해서
//    이월이라는건 처리되지않은 건에 대해서 발생하는 것 아닌가? true 혹은 false로 처리된 데이터에 대해서 이월이라는 개념이 존재할 수 있는가
    // 일단 지금은 밤이 깊었으니 3개 모두 다 금달을 기준으로 조회하게 하고 추후에 수정하는 방향으로
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

        System.out.println("출고 대기 : "+needApprovalCount);
        System.out.println("출고 승인 : "+approvedCount);
        System.out.println("출고 거절 : "+deniedCount);
        return new TotalListResponseDto(needApprovalCount,approvedCount,deniedCount);
    }
    @Transactional
    public SuccessCode releaseDecide(Long id, ReleaseDecidereqDto releaseDecidereqDto) {
        Purchase purchase=purchasesRepository.findById(id).orElseThrow(
                () -> new AdminNotFoundException(NOT_FOUND_DATA)
        );
        if(releaseDecidereqDto.getApprove()){
            purchase.update(releaseDecidereqDto.getApprove(),releaseDecidereqDto.getDeliveryDate());
            return SuccessCode.PURCHASE_APPROVE;
        }
        else{
            purchase.update(releaseDecidereqDto.getApprove(),releaseDecidereqDto.getDenyMessage());
            return SuccessCode.PURCHASE_DENIED;
        }
    }
}
