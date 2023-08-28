package com.example.finalproject.domain.admin.dto;

import lombok.Getter;

@Getter
public class YearsPurchaseStatDto {
    private Integer year;
    private Long count;

    public YearsPurchaseStatDto(Integer year, Long count) {
        this.year = year;
        this.count = count;
    }
}
