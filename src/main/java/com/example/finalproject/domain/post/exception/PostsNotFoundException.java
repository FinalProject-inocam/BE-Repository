package com.example.finalproject.domain.post.exception;

import com.example.finalproject.global.enums.ErrorCode;
import com.example.finalproject.global.exception.GlobalException;

public class PostsNotFoundException extends GlobalException {
    public PostsNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
