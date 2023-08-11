package com.example.finalproject.domain.admin.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PurchaseByApproveDto {
    private List<Long> purchase;
    private List<Long> approve;
    private List<Long> cancel;

    public PurchaseByApproveDto (List<Long> purchase, List<Long> approve, List<Long> cancel) {
        this.purchase = purchase;
        this.approve = approve;
        this.cancel = cancel;
    }
}
