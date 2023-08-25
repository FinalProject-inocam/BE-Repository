package com.example.finalproject.domain.shop.exception;

import com.example.finalproject.global.enums.ErrorCode;
import com.example.finalproject.global.exception.GlobalException;

public class ReviewImageLimitException extends GlobalException {
    public ReviewImageLimitException(ErrorCode errorCode) {
        super(errorCode);
    }
}
