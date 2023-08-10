package com.example.finalproject.domain.admin.dto;

import lombok.Getter;

@Getter
public class TotalListResponseDto {
    private Long approve;
    private Long decide;
    private Long deny;
    public TotalListResponseDto(Long approve,Long decide,Long deny){
        this.approve=approve;
        this.decide=decide;
        this.deny=deny;
    }
}
