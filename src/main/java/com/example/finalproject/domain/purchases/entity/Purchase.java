package com.example.finalproject.domain.purchases.entity;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.purchases.dto.request.PurchasesRequestDto;
import com.example.finalproject.global.enums.UserGenderEnum;
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
    @Enumerated(value = EnumType.STRING)
    private UserGenderEnum gender;

    @Column(nullable = false)
    private Integer birthYear;

    @Column(nullable = false)
    private Boolean alarm;

    private String content;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String addressName;

    @Column(nullable = false)
    private String zoneNo;

    private Boolean approve;

    private String denyMessage;

    private Date deliveryDate;

    private String trim;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    public Purchase(PurchasesRequestDto purchasesRequestDto, User user) {
        this.type = purchasesRequestDto.getType();
        this.color = purchasesRequestDto.getColor();
        this.gender = genderEnumCheck(purchasesRequestDto.getGender());
        this.birthYear = purchasesRequestDto.getBirthYear();
        this.alarm = purchasesRequestDto.getAlarm();
        this.phoneNumber = purchasesRequestDto.getPhoneNumber();
        this.content = purchasesRequestDto.getContent();
        this.addressName = purchasesRequestDto.getAddressName();
        this.zoneNo = purchasesRequestDto.getZoneNo();
        this.price = price;
        this.user = user;
        this.trim=purchasesRequestDto.getTrim();
    }

    public void update(Boolean approve, Date deliveryDate) {
        this.approve = approve;
        this.deliveryDate = deliveryDate;
    }

    public void update(Boolean approve, String denyMessage) {
        this.approve = approve;
        this.denyMessage = denyMessage;
    }

    public void update(PurchasesRequestDto purchasesRequestDto) {
        this.type = purchasesRequestDto.getType();
        this.color = purchasesRequestDto.getColor();
        this.gender = genderEnumCheck(purchasesRequestDto.getGender());
        this.birthYear = purchasesRequestDto.getBirthYear();
        this.alarm = purchasesRequestDto.getAlarm();
        this.content = purchasesRequestDto.getContent();
        this.addressName = purchasesRequestDto.getAddressName();
        this.zoneNo = purchasesRequestDto.getZoneNo();
    }

    private UserGenderEnum genderEnumCheck(String genderStr) {
        if (genderStr.equals("male")) {
            return UserGenderEnum.MALE;
        }
        if (genderStr.equals("female")) {
            return UserGenderEnum.FEMALE;
        }
        if (genderStr.equals("company")) {
            return UserGenderEnum.COMPANY;
        }
        return null;
    }
}
