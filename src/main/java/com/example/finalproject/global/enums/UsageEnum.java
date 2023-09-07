package com.example.finalproject.global.enums;

import lombok.Getter;

@Getter
public enum UsageEnum {
    COMPANY("COMPANY"),
    PERSONAL("PERSONAL");


    private final String usage;

    UsageEnum(String usage){
        this.usage = usage;
    }
}
