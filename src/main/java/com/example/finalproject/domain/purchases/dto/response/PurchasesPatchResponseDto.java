package com.example.finalproject.domain.purchases.dto.response;

import com.example.finalproject.domain.purchases.entity.Purchase;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@NoArgsConstructor
public class PurchasesPatchResponseDto {
    private Long purchaseId;
    private String type;
    private String color;
    private Boolean alarm;
    private String content;
    private String addressName;
    private String zoneNo;
    private Boolean approve;
    private String denyMessage;
    private Date deliveryDate;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public PurchasesPatchResponseDto(Purchase purchase) {
        this.purchaseId = purchase.getPurchaseId();
        this.type = purchase.getType();
        this.color = purchase.getColor();
        this.alarm = purchase.getAlarm();
        this.content = purchase.getContent();
        this.addressName = purchase.getAddressName();
        this.zoneNo = purchase.getZoneNo();
        this.approve = purchase.getApprove();
        this.denyMessage = purchase.getDenyMessage();
        this.deliveryDate = purchase.getDeliveryDate();
        this.createdAt = purchase.getCreatedAt();
        this.modifiedAt = purchase.getModifiedAt();
    }
}
