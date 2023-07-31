package com.example.finalproject.auth.service;

import com.example.finalproject.auth.entity.RefreshToken;
import com.example.finalproject.global.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate redisTemplate;
    private final JwtUtil jwtUtil;

    @Transactional
    public void setRefreshToken(RefreshToken refreshToken) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(refreshToken.getRefreshToken(), String.valueOf(refreshToken.getAccessToken()));
        redisTemplate.expire(String.valueOf(refreshToken.getAccessToken()), (60 * 60 * 24 * 14 + 60), TimeUnit.SECONDS); // 2주 refreshtoken
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


    public Boolean limitAccess(String nickname) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        int count = 0;
// Redis에서 key를 찾기 위해 모든 키를 순회합니다.
        Set<String> keys = redisTemplate.keys("*");
        for (String key : keys) {
            String refreshToken = key.substring(7);
            Claims info = jwtUtil.getUserInfoFromToken(refreshToken);
            String usernameFromRefreshToken = info.get("nickname", String.class);
            if (usernameFromRefreshToken.equals(nickname)) {
                count++;
            }
        }
        return count > 4 ? true : false;
    }
}