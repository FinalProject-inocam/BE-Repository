package com.example.finalproject.domain.admin.dto;

import com.example.finalproject.domain.purchases.entity.Purchase;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AllPurchasseResponseDto {
    private Long id;
    private LocalDateTime createAt;
    private String nickname;
    private String type;
    private Boolean approve;

    //id, 신청날짜, 이름, 차량종류, 현제상태(승인여부)
    public AllPurchasseResponseDto(Purchase purchase) {
        this.id = purchase.getPurchaseId();
        this.createAt = purchase.getCreatedAt();
        this.approve = purchase.getApprove();
        this.type = purchase.getType();
        this.nickname = purchase.getUser().getNickname();
    }
}
