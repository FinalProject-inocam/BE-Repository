package com.example.finalproject.domain.shop.exception;

import com.example.finalproject.global.enums.ErrorCode;
import com.example.finalproject.global.exception.GlobalException;

public class ReviewAuthorityException extends GlobalException {
    public ReviewAuthorityException(ErrorCode errorCode) {
        super(errorCode);
    }
}
