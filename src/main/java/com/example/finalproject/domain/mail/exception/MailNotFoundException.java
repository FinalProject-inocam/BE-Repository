package com.example.finalproject.domain.mail.exception;

import com.example.finalproject.global.enums.ErrorCode;
import com.example.finalproject.global.exception.GlobalException;

public class MailNotFoundException extends GlobalException {
    public MailNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}