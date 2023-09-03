package com.example.finalproject.domain.sockettest.dto;

import com.example.finalproject.domain.purchases.entity.Purchase;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
public class PurchaseResponseDtoSocket {
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
    private String trim;
    private String createdAt;
    private String modifiedAt;

    public PurchaseResponseDtoSocket(Purchase purchase) {
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
        this.createdAt = String.valueOf(purchase.getCreatedAt());
        this.modifiedAt = String.valueOf(purchase.getModifiedAt());
    }

}
