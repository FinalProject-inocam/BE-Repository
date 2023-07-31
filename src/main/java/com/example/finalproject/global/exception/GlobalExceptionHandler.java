package com.example.finalproject.global.exception;

import com.example.finalproject.global.responsedto.ApiResponse;
import com.example.finalproject.global.utils.ResponseUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // 일반적인 클라이언트의 잘못된 요청 시
    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<?> handleException(IllegalArgumentException e){
        return ResponseUtils.error(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    // 사용자가 제출한 데이터로 해당 객체를 찾을 수 없을 때
    @ExceptionHandler(NullPointerException.class)
    public ApiResponse<?> handleException(NullPointerException e){
        return ResponseUtils.error(HttpStatus.NOT_FOUND, e.getMessage());
    }

    // request 입력시 올바르지 않은 값일 경우 (valid 관련)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handleException(MethodArgumentNotValidException e){
        StringBuilder sb = new StringBuilder();
        e.getFieldErrors().forEach((ex) -> {
            sb.append(ex.getDefaultMessage()).append("/");
        });
        sb.setLength(sb.length() - 1);
        return ResponseUtils.error(HttpStatus.BAD_REQUEST, sb.toString());
    }

    // 권한 요청이 잘못들어왔을 경우(게시글의 생성은 host만)
    @ExceptionHandler(AccessDeniedException.class)
    public ApiResponse<?> handleException(AccessDeniedException e){
        return ResponseUtils.error(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

}
