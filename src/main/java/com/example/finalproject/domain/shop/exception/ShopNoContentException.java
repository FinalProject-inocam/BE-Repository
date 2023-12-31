package com.example.finalproject.domain.shop.exception;

import com.example.finalproject.global.enums.SuccessCode;

public class ShopNoContentException extends RuntimeException {
    private final SuccessCode successCode;

    public ShopNoContentException(SuccessCode successCode) {
        super(successCode.getDetail());
        this.successCode = successCode;
    }
}
