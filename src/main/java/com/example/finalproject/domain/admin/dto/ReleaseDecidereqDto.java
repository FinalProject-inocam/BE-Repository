package com.example.finalproject.domain.admin.dto;

import jakarta.persistence.Column;
import lombok.Getter;

import java.util.Date;

@Getter
public class ReleaseDecidereqDto {
    @Column(nullable = false)
    private Boolean approve;

    private String denyMessage;
    private Date deliveryDate;

}
