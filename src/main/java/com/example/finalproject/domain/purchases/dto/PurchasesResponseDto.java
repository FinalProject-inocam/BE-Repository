package com.example.finalproject.domain.purchases.dto;

import com.example.finalproject.domain.purchases.entity.Purchases;
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

    public PurchasesResponseDto(Purchases purchases) {
        this.id = purchases.getId();
        this.type = purchases.getType();
        this.price = purchases.getPrice();
        this.color = purchases.getColor();
        this.alarm = purchases.getAlarm();
        this.content = purchases.getContent();
        this.address_name = purchases.getAddress_name();
        this.zone_no = purchases.getZone_no();
        this.approve = purchases.getApprove();
        this.deny_message = purchases.getDeny_message();
        this.delivery_date = purchases.getDelivery_date();
        this.createdAt = purchases.getCreatedAt();
        this.modifiedAt = purchases.getModifiedAt();
    }

}
