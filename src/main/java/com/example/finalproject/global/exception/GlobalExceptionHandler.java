package com.example.finalproject.global.exception;


import com.example.finalproject.domain.post.exception.PostsNotFoundException;
import com.example.finalproject.global.responsedto.ApiResponse;
import com.example.finalproject.global.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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



    @ExceptionHandler(PostsNotFoundException.class)
    public ApiResponse<?> handleNewsNotFoundException(PostsNotFoundException e) {
        log.info(e.getMessage());
        return ResponseUtils.error(e.getErrorCode());
    }



    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> validationExceptionHandler(MethodArgumentNotValidException e) {
        Map<String, String> errors = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(
                        error.getField(), error.getDefaultMessage()
                ));
        return ResponseUtils.error(HttpStatus.BAD_REQUEST, errors);
    }
}
