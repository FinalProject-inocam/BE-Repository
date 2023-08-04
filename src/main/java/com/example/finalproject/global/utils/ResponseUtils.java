package com.example.finalproject.global.utils;

import com.example.finalproject.global.enums.ErrorCode;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.responsedto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Slf4j
public class ResponseUtils {

    public static <T> ApiResponse<T> ok(T response) {
        return new ApiResponse<>(true, 200, null, response);
    }

    public static ApiResponse<?> ok(SuccessCode successCode) {
        int statusCode = successCode.getHttpStatus().value();
        String msg = successCode.getDetail();
        return new ApiResponse<>(true, statusCode, msg, null);
    }

    public static ApiResponse<?> error(ErrorCode errorCode) {
        int statusCode = errorCode.getHttpStatus().value();
        String msg = errorCode.getDetail();
        return new ApiResponse<>(false, statusCode, msg, null);
    }

//    public static ApiResponse<?> error(HttpStatus httpStatus, Map<String, String> errors) {
//        return new ApiResponse<>(false, httpStatus.value(), errors, null);
//    }

    public static ApiResponse<?> error(HttpStatus httpStatus, String error) {
        return new ApiResponse<>(false, httpStatus.value(), error, null);
    }
}
