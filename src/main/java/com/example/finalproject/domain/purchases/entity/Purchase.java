package com.example.finalproject.domain.purchases.entity;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.purchases.dto.request.PurchasesRequestDto;
import com.example.finalproject.domain.purchases.dto.request.PurchasesUpdateRequestDto;
import com.example.finalproject.global.enums.UsageEnum;
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

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UsageEnum usageEnum;

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
        this.price = purchasesRequestDto.getPrice();
        this.user = user;
        this.usageEnum = usageEnumCheck(purchasesRequestDto.getUsage());
        this.trim = purchasesRequestDto.getTrim();
        this.name = purchasesRequestDto.getName();
    }

    public void update(Boolean approve, Date deliveryDate) {
        this.approve = approve;
        this.deliveryDate = deliveryDate;
    }

    public void update(Boolean approve, String denyMessage) {
        this.approve = approve;
        this.denyMessage = denyMessage;
    }

    public void update(PurchasesUpdateRequestDto purchasesRequestDto) {
        this.color = purchasesRequestDto.getColor();
        this.content = purchasesRequestDto.getContent();
        this.name = purchasesRequestDto.getName();
        this.phoneNumber = purchasesRequestDto.getPhoneNumber();
        this.addressName = purchasesRequestDto.getAddressName();
        this.zoneNo = purchasesRequestDto.getZoneNo();
        this.alarm = purchasesRequestDto.getAlarm();
    }

    private UserGenderEnum genderEnumCheck(String genderStr) {
        if (genderStr.equals("MALE")) {
            return UserGenderEnum.MALE;
        }
        if (genderStr.equals("FEMALE")) {
            return UserGenderEnum.FEMALE;
        }
        throw new IllegalArgumentException("성별값이 옳지 않습니다.");
    }

    private UsageEnum usageEnumCheck(String usageStr) {
        if (usageStr.equals("PERSONAL")) {
            return UsageEnum.PERSONAL;
        }
        if (usageStr.equals("COMPANY")) {
            return UsageEnum.COMPANY;
        }
        throw new IllegalArgumentException("값이 옳지 않습니다.");
    }
}
