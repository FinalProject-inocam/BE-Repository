package com.example.finalproject.domain.purchases.exception;

import com.example.finalproject.global.enums.ErrorCode;
import com.example.finalproject.global.exception.GlobalException;

public class PurchasesNotFoundException extends GlobalException {
    public PurchasesNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}

