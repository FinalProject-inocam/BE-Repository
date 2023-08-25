package com.example.finalproject.global.exception;


import com.example.finalproject.domain.mail.exception.MailNotFoundException;
import com.example.finalproject.domain.shop.exception.ReviewAuthorityException;
import com.example.finalproject.domain.shop.exception.ShopNoContentException;
import com.example.finalproject.global.enums.ErrorCode;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.exception.buisnessException.ConditionDisagreeException;
import com.example.finalproject.global.responsedto.ApiResponse;
import com.example.finalproject.global.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

/**
 * ex)
 * <pre>
 * &#64;ExceptionHandler(UserException.class)
 * public ApiResponse<?> handleUserException(UserException e) {
 *      return ResponseUtils.error(e.getErrorCode());
 * }
 * </pre>
 */
@Slf4j(topic = "global exception handler")
@RestControllerAdvice
public class GlobalExceptionHandler {


    // 일반적인 클라이언트의 잘못된 요청 시
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleException(IllegalArgumentException e) {
        log.error("일반적인 클라이언트의 잘못된 요청 시 : " + e.getMessage());
        return ResponseUtils.error(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    // 사용자가 제출한 데이터로 해당 객체를 찾을 수 없을 때
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<?> handleException(NullPointerException e) {
        log.error("사용자가 제출한 데이터로 해당 객체를 찾을 수 없을 때 : " + e.getMessage());
        return ResponseUtils.error(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(MailNotFoundException.class)
    public ApiResponse<?> handleNewsNotFoundException(MailNotFoundException e) {
        log.info("존재하지 않는 메일 : " + e.getMessage());
        return ResponseUtils.error(e.getErrorCode());
    }

    // 권한 요청이 잘못들어왔을 경우(게시글의 생성은 host만)
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<?> handleException(AccessDeniedException e) {
        log.error("권한 요청이 잘못들어왔을 경우 : " + e.getMessage());
        return ResponseUtils.error(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    // valid를 만족하지 못했을때
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> validationExceptionHandler(MethodArgumentNotValidException e) {
        String error = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        log.error("valid를 만족하지 못했을때 : " + error);
        return ResponseUtils.error(HttpStatus.BAD_REQUEST, error);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> requestParameterExceptionHandler(MissingServletRequestParameterException e) {
        log.error("requestParameter 오류 : " + e.getMessage());
        return ResponseUtils.error(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    // No_CONTENT는 기본적으로 응답값이 없음.
    @ExceptionHandler(ShopNoContentException.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<?> JsonParseExceptionHandler(ShopNoContentException e) {
        log.error("주변에 조회 가능한 가게가 없습니다. : " + e.getMessage());
        return ResponseUtils.ok(SuccessCode.NO_SHOP_SUCCESS);
    }

    @ExceptionHandler(JSONException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> JsonParseExceptionHandler(JSONException e) {
        log.error("존재하지 않는 shopId : " + e.getMessage());
        return ResponseUtils.error(ErrorCode.SHOP_NOT_FOUND);
    }

    @ExceptionHandler(ReviewAuthorityException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<?> JsonParseExceptionHandler(ReviewAuthorityException e) {
        log.error("권한없는 유저 : " + e.getMessage());
        return ResponseUtils.error(e.getErrorCode());
    }

    @ExceptionHandler(ConditionDisagreeException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<?> ConditionDisagreeExceptionHandler(ConditionDisagreeException e) {
        log.error("소셜 로그인 한 사용자가 모든 항목에 동의 하지 않음 : " + e.getMessage());
        return ResponseUtils.error(ErrorCode.MORE_AGREEMENT_NEEDED);
    }
}
