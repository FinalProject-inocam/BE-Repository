package com.example.finalproject.auth.security;

import com.example.finalproject.auth.dto.LoginRequestDto;
import com.example.finalproject.auth.entity.RefreshToken;
import com.example.finalproject.auth.entity.User;
import com.example.finalproject.auth.service.RedisService;
import com.example.finalproject.global.enums.ErrorCode;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.enums.UserRoleEnum;
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
        String email = user.getEmail();
        String nickname = user.getNickname();
        UserRoleEnum role = user.getRole();

        String accessToken = jwtUtil.createAccessToken(email, nickname, role);
        response.addHeader(JwtUtil.ACCESS_TOKEN, accessToken);

        // 중복 로그인 가능한 계정 수를 제한시키기
        if (redisService.limitAccess(nickname)) {
            log.info("접속수 제한 초과");
            redisService.deleteOldRefreshToken(nickname);
        }
        String newRefreshToken = jwtUtil.createRefreshToken(email, nickname, role);
        response.addHeader(JwtUtil.REFRESH_TOKEN, newRefreshToken);
        // redis에 저장
        redisService.setRefreshToken(new RefreshToken(newRefreshToken, accessToken));


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