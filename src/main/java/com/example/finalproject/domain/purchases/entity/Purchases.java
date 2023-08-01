package com.example.finalproject.domain.purchases.entity;

import com.example.finalproject.auth.entity.User;
import com.example.finalproject.domain.purchases.dto.PurchasesRequestDto;
import com.example.finalproject.global.utils.Timestamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
public class Purchases extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    private String address_name;

    @Column(nullable = false)
    private String zone_no;

    private Boolean approve;

    private String deny_message;

    private Date delivery_date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Purchases(PurchasesRequestDto purchasesRequestDto, User user) {
        this.type = purchasesRequestDto.getType();
        this.color = purchasesRequestDto.getColor();
        this.alarm = purchasesRequestDto.getAlarm();
        this.content = purchasesRequestDto.getContent();
        this.address_name = purchasesRequestDto.getAddress_name();
        this.zone_no = purchasesRequestDto.getZone_no();
        this.user = user;
    }

    public void update(PurchasesRequestDto purchasesRequestDto) {
        this.type = purchasesRequestDto.getType();
        this.color = purchasesRequestDto.getColor();
        this.alarm = purchasesRequestDto.getAlarm();
        this.content = purchasesRequestDto.getContent();
        this.address_name = purchasesRequestDto.getAddress_name();
        this.zone_no = purchasesRequestDto.getZone_no();
    }
}
