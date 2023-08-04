package com.example.finalproject.domain.purchases.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PurchasesRequestDto {
    private String type;
    private String color;
    private Boolean alarm;
    private String content;
    private String addressName;
    private String zoneNo;
}
