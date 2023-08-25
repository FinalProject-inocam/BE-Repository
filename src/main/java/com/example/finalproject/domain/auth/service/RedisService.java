package com.example.finalproject.domain.auth.service;

import com.example.finalproject.domain.auth.entity.RefreshToken;
import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.global.enums.UserRoleEnum;
import com.example.finalproject.global.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate redisTemplate;
    private final JwtUtil jwtUtil;

    public void newLogin(User user, HttpServletResponse response) {
        String email = user.getEmail();
        String nickname = user.getNickname();
        UserRoleEnum role = user.getRole();

        String accessToken = jwtUtil.createAccessToken(email, nickname, role);
        log.info("expired error : " + accessToken);
        response.addHeader(JwtUtil.ACCESS_TOKEN, accessToken);

        // 중복 로그인 가능한 계정 수를 제한시키기
        if (limitAccess(nickname)) {
            log.info("접속수 제한 초과");
            deleteOldRefreshToken(nickname);
        }
        log.info("333");
        String newRefreshToken = jwtUtil.createRefreshToken(email, nickname, role);
        response.addHeader(JwtUtil.REFRESH_TOKEN, newRefreshToken);
        // redis에 저장
        setRefreshToken(new RefreshToken(newRefreshToken, accessToken));
    }

    @Transactional
    public void setRefreshToken(RefreshToken refreshToken) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        Duration expireDuration = Duration.ofSeconds(jwtUtil.REFRESH_TOKEN_TIME);
        values.set(refreshToken.getRefreshToken(), String.valueOf(refreshToken.getAccessToken()), expireDuration);
    }

    public void deleteOldRefreshToken(String nickname) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        Set<String> keys = redisTemplate.keys("*");
        for (String key : keys) {
            String refreshToken = key.substring(7);
            Claims info = jwtUtil.getUserInfoFromToken(refreshToken);
            String usernameFromRefreshToken = info.get("nickname", String.class);
            if (usernameFromRefreshToken.equals(nickname)) {
                redisTemplate.delete(key);
                return;
            }

        }
    }

    public void deleteAllRefreshToken(String nickname) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        Set<String> keys = redisTemplate.keys("*");
        for (String key : keys) {
            String refreshToken = key.substring(7);
            Claims info = jwtUtil.getUserInfoFromToken(refreshToken);
            String usernameFromRefreshToken = info.get("nickname", String.class);
            if (usernameFromRefreshToken.equals(nickname)) {
                redisTemplate.delete(key);
            }
        }
    }


    public Boolean limitAccess(String nickname) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        int count = 0;
        // Redis에서 key를 찾기 위해 모든 키를 순회합니다.
        Set<String> keys = redisTemplate.keys("*");
        for (String key : keys) {
            String refreshToken = key.substring(7);
            log.info("expired test1");
            Claims info = jwtUtil.getUserInfoFromToken(refreshToken);
            log.info("expired test2");
            String usernameFromRefreshToken = info.get("nickname", String.class);
            if (usernameFromRefreshToken.equals(nickname)) {
                count++;
            }
        }
        return count > 4 ? true : false;
    }
}