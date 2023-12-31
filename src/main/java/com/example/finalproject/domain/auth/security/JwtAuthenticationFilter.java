package com.example.finalproject.domain.auth.security;

import com.example.finalproject.domain.auth.dto.LoginRequestDto;
import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.auth.service.RedisService;
import com.example.finalproject.global.enums.ErrorCode;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.utils.JwtUtil;
import com.example.finalproject.global.utils.ResponseUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j(topic = "JWT Authentication")
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;
    private final RedisService redisService;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("attemptAuthentication");
        try {
            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    requestDto.getEmail(),
                    requestDto.getPassword()
            );
            return getAuthenticationManager().authenticate(usernamePasswordAuthenticationToken);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        log.info("successfulAuthentication");
        User user = ((UserDetailsImpl) authResult.getPrincipal()).getUser();
        // 로그인 절차
        redisService.newLogin(user, response, request);

        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        new ObjectMapper().writeValue(response.getOutputStream(),
                ResponseUtils.ok(SuccessCode.USER_LOGIN_SUCCESS)
        );

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        log.info("unsuccessfulAuthentication");
        response.setStatus(400);
        response.setContentType("application/json;charset=UTF-8");
        new ObjectMapper().writeValue(response.getOutputStream(),
                ResponseUtils.error(ErrorCode.USER_LOGIN_FAIL)
        );
    }

}