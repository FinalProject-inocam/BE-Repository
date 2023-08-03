package com.example.finalproject.domain.purchases.dto;

import com.example.finalproject.domain.purchases.entity.Purchase;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@NoArgsConstructor
public class PurchasesResponseDto {
    private Long id;
    private String type;
    private Long price;
    private String color;
    private Boolean alarm;
    private String content;
    private String address_name;
    private String zone_no;
    private Boolean approve;
    private String deny_message;
    private Date delivery_date;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public PurchasesResponseDto(Purchase purchase) {
        this.id = purchase.getId();
        this.type = purchase.getType();
        this.price = purchase.getPrice();
        this.color = purchase.getColor();
        this.alarm = purchase.getAlarm();
        this.content = purchase.getContent();
        this.address_name = purchase.getAddress_name();
        this.zone_no = purchase.getZone_no();
        this.approve = purchase.getApprove();
        this.deny_message = purchase.getDeny_message();
        this.delivery_date = purchase.getDelivery_date();
        this.createdAt = purchase.getCreatedAt();
        this.modifiedAt = purchase.getModifiedAt();
    }

}
