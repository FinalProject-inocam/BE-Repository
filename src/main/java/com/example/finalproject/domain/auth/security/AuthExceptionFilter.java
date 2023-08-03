package com.example.finalproject.domain.auth.security;

import com.example.finalproject.global.utils.ResponseUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "AuthFilter exception")
//@Component
@RequiredArgsConstructor
public class AuthExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        //다음 필터인 AuthFilter를 try하고 AuthFilter에서 던진 에러를 여기서 캐치한다.
        try{
            log.info("authException");
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            // 현재 확인된 exception이... UsernameNotFoundException 뿐인것 같은데...
            log.error(e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setContentType("application/json;charset=UTF-8");

            new ObjectMapper().writeValue(response.getOutputStream(),
                    ResponseUtils.error(HttpStatus.BAD_REQUEST, e.getMessage())
            );
        }
    }
}