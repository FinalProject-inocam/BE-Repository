package com.example.finalproject.global.exception;


import com.example.finalproject.domain.mail.exception.MailNotFoundException;
import com.example.finalproject.global.responsedto.ApiResponse;
import com.example.finalproject.global.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.LinkedHashMap;
import java.util.Map;

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
        log.error("일반적인 클라이언트의 잘못된 요청 시");
        return ResponseUtils.error(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    // 사용자가 제출한 데이터로 해당 객체를 찾을 수 없을 때
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<?> handleException(NullPointerException e) {
        log.error("사용자가 제출한 데이터로 해당 객체를 찾을 수 없을 때");
        return ResponseUtils.error(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(MailNotFoundException.class)
    public ApiResponse<?> handleNewsNotFoundException(MailNotFoundException e) {
        log.info(e.getMessage());
        return ResponseUtils.error(e.getErrorCode());
    }
//     // request 입력시 올바르지 않은 값일 경우 (valid 관련)
//     @ExceptionHandler(MethodArgumentNotValidException.class)
//     public ApiResponse<?> handleException(MethodArgumentNotValidException e){
//         StringBuilder sb = new StringBuilder();
//         e.getFieldErrors().forEach((ex) -> {
//             sb.append(ex.getDefaultMessage()).append("/");
//         });
//         sb.setLength(sb.length() - 1);
//         return ResponseUtils.error(HttpStatus.BAD_REQUEST, sb.toString());
//     }

    // 권한 요청이 잘못들어왔을 경우(게시글의 생성은 host만)
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<?> handleException(AccessDeniedException e) {
        log.error("권한 요청이 잘못들어왔을 경우");
        return ResponseUtils.error(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

//    @ExceptionHandler(PostsNotFoundException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public ApiResponse<?> handleNewsNotFoundException(PostsNotFoundException e) {
//        log.info(e.getMessage());
//        return ResponseUtils.error(e.getErrorCode());
//    }

    // valid를 만족하지 못했을때
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> validationExceptionHandler(MethodArgumentNotValidException e) {
        log.error("valid를 만족하지 못했을때");
//        Map<String, String> errors = new LinkedHashMap<>();
//        e.getBindingResult().getFieldErrors()
//                .forEach(error -> errors.put(
//                        error.getField(), error.getDefaultMessage()
//                ));
        String error = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return ResponseUtils.error(HttpStatus.BAD_REQUEST, error);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> requestParameterExceptionHandler(MissingServletRequestParameterException e) {
        log.error("requestParameter 오류");
        return ResponseUtils.error(HttpStatus.BAD_REQUEST, e.getMessage());
    }
}
