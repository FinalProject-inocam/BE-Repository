package com.example.finalproject.domain.purchases.entity;

import com.example.finalproject.domain.admin.dto.ReleaseDecidereqDto;
import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.purchases.dto.PurchasesRequestDto;
import com.example.finalproject.global.utils.Timestamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
public class Purchase extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long purchaseId;

    @Column(nullable = false)
    private String type;

    private Long price;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private Boolean alarm;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String addressName;

    @Column(nullable = false)
    private String zoneNo;

    private Boolean approve;

    private String denyMessage;

    private Date deliveryDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    public Purchase(PurchasesRequestDto purchasesRequestDto, User user,Long price) {
        this.type = purchasesRequestDto.getType();
        this.color = purchasesRequestDto.getColor();
        this.alarm = purchasesRequestDto.getAlarm();
        this.content = purchasesRequestDto.getContent();
        this.addressName = purchasesRequestDto.getAddressName();
        this.zoneNo = purchasesRequestDto.getZoneNo();
        this.price=price;
        this.user = user;
    }

    public void update(Boolean approve,Date deliveryDate){
        this.approve=approve;
        this.deliveryDate=deliveryDate;
    }
    public void update(Boolean approve,String denyMessage){
        this.approve=approve;
        this.denyMessage=denyMessage;
    }
    public void update(PurchasesRequestDto purchasesRequestDto) {
        this.type = purchasesRequestDto.getType();
        this.color = purchasesRequestDto.getColor();
        this.alarm = purchasesRequestDto.getAlarm();
        this.content = purchasesRequestDto.getContent();
        this.addressName = purchasesRequestDto.getAddressName();
        this.zoneNo = purchasesRequestDto.getZoneNo();
    }
}
