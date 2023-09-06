package com.example.finalproject.domain.purchases.dto.response;

import com.example.finalproject.domain.purchases.entity.Purchase;
import com.example.finalproject.global.enums.UsageEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@NoArgsConstructor
public class PurchasesResponseDto {
    private Long purchaseId;
    private String type;
    private Long price;
    private String color;
    private Boolean alarm;
    private String content;
    private String phoneNumber;
    private String addressName;
    private String zoneNo;
    private Boolean approve;
    private String denyMessage;
    private Date deliveryDate;
    private String name;
    private UsageEnum usage;
    private String trim;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public PurchasesResponseDto(Purchase purchase) {
        this.name = purchase.getName();
        this.usage = purchase.getUsageEnum();
        this.trim = purchase.getTrim();
        this.purchaseId = purchase.getPurchaseId();
        this.type = purchase.getType();
        this.price = purchase.getPrice();
        this.color = purchase.getColor();
        this.alarm = purchase.getAlarm();
        this.content = purchase.getContent();
        this.phoneNumber = purchase.getPhoneNumber();
        this.addressName = purchase.getAddressName();
        this.zoneNo = purchase.getZoneNo();
        this.approve = purchase.getApprove();
        this.denyMessage = purchase.getDenyMessage();
        this.deliveryDate = purchase.getDeliveryDate();
        this.createdAt = purchase.getCreatedAt();
        this.modifiedAt = purchase.getModifiedAt();
    }

}
