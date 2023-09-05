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
    private String deliveryDate;
    private String trim;
    private String createdAt;
    private String modifiedAt;

    public PurchaseResponseDtoSocket(Purchase purchase) {
        this.purchaseId = purchase.getPurchaseId();
        this.type = purchase.getType();
        this.price = purchase.getPrice();
        this.color = purchase.getColor();
        this.alarm = purchase.getAlarm();
        this.content = purchase.getContent() != null ? purchase.getContent() : "";
        this.phoneNumber = purchase.getPhoneNumber();
        this.addressName = purchase.getAddressName();
        this.zoneNo = purchase.getZoneNo();
        String deliveryDateStr = "";
        if (purchase.getApprove() != null) {
            if (purchase.getApprove()) {
                deliveryDateStr = String.valueOf(purchase.getDeliveryDate());
            }
            if (!purchase.getApprove()) {
                deliveryDateStr = purchase.getDenyMessage();
            }
        }
        this.deliveryDate = deliveryDateStr;
        this.trim = purchase.getTrim() != null ? purchase.getTrim() : "";
        this.createdAt = purchase.getCreatedAt() != null ? String.valueOf(purchase.getCreatedAt()) : "";
        this.modifiedAt = purchase.getModifiedAt() != null ? String.valueOf(purchase.getModifiedAt()) : "";
    }

}
