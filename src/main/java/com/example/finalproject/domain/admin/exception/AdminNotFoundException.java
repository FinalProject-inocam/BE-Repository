package com.example.finalproject.domain.admin.exception;

import com.example.finalproject.global.enums.ErrorCode;
import com.example.finalproject.global.exception.GlobalException;

public class AdminNotFoundException extends GlobalException {
    public AdminNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

}
