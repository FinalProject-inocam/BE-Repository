package com.example.finalproject.global.utils;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.auth.repository.UserRepository;
import com.example.finalproject.global.enums.UserRoleEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Slf4j(topic = "JwtUtil")
@Component
@RequiredArgsConstructor
public class JwtUtil {
    // Header KEY 값
    public static final String ACCESS_TOKEN = "Authorization";
    public static final String REFRESH_TOKEN = "Refresh";
    // 사용자 권한 값의 KEY
    public static final String NICKNAME = "nickname";
    //
    public static final String EMAIL = "email";
    // Token 식별자
    public static final String BEARER_PREFIX = "Bearer ";

    // accessToken 만료시간
    private final long TOKEN_TIME = 60 * 60 * 1000L; // 한시간
//    private final long TOKEN_TIME = 30 * 1000L; // 1분

    // refreshToken 만료시간 다른 곳에서 쓰려고 public 했는데 이게 맞나...?
    public final long REFRESH_TOKEN_TIME = 14 * 24 * 60 * 60 * 1000L; //2주

    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    private final UserRepository userRepository;
    private final RedisTemplate redisTemplate;

    // 로그 설정
    public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String createAccessToken(String email, String nickname, UserRoleEnum role) {
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(role.getAuthority()) // 권한
                        .claim(NICKNAME, nickname) // 닉네임
                        .claim(EMAIL, email) // 이메일
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME)) // 만료 시간
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                        .compact();
    }


    public String createRefreshToken(String email, String nickname, UserRoleEnum role) {
        Date now = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(role.getAuthority()) // 권한
                        .claim(EMAIL, email) // 이메일
                        .claim(NICKNAME, nickname) // 닉네임
                        .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_TIME)) // 만료 시간
                        .setIssuedAt(now) // 발급일
                        .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                        .compact();
    }

    // header 에서 JWT 가져오기
    public String getAccessTokenFromHeader(HttpServletRequest request) {
        String accessToken = request.getHeader(ACCESS_TOKEN);
        if (StringUtils.hasText(accessToken) && accessToken.startsWith(BEARER_PREFIX)) {
            return accessToken.substring(7);
        }
        return null;
    }

    public String getRefreshTokenFromHeader(HttpServletRequest request) {

        String refreshToken = request.getHeader(REFRESH_TOKEN);
        if (StringUtils.hasText(refreshToken)) {
            return refreshToken;
        }
        return null;
    }

    // JWT Bearer Substirng 메서드
    public String substringToken(String bearerToken) {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        throw new NullPointerException("토큰의 값이 존재하지 않습니다.");
    }

    // 토큰 검증
    public Boolean validateAccessToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            logger.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    public String regenerateAccessToken(String refreshToken) {
        // 토큰 재발급 과정

        String email = getUserInfoFromToken(refreshToken.substring(7)).get("email", String.class);
        Optional<User> userOptional = userRepository.findByEmail(email);
        String nickname = userOptional.get().getNickname();
        UserRoleEnum userRole = userOptional.get().getRole();
        return createAccessToken(email, nickname, userRole);

    }

    public String regenerateRefreshToken(String refreshToken) {
        String email = getUserInfoFromToken(substringToken(refreshToken)).get("email", String.class);
        Optional<User> userOptional = userRepository.findByEmail(email);
        String nickname = userOptional.get().getNickname();
        UserRoleEnum userRole = userOptional.get().getRole();
        return createRefreshToken(email, nickname, userRole);
    }

    public String findRefreshToken(String refreshToken) {
        String redisRefreshToken = getAccessTokenFromRedis(refreshToken);
        if (redisRefreshToken == null) {
            throw new RuntimeException("저장되지 않은 RefreshToken 입니다.");
        }
        return redisRefreshToken;
    }

    public void validateRefreshToken(String refreshToken) {
        String accessTokenFromRedis = getAccessTokenFromRedis(refreshToken);
        log.info(accessTokenFromRedis);

        if (accessTokenFromRedis == null) {
            throw new RuntimeException("저장되지 않은 RefreshToken 입니다.");
        }

        String tokenValue = accessTokenFromRedis.substring(7);
        if (validateAccessToken(tokenValue)) {
            log.info("아직 유효한 accessToken이 있는 상태에서 새로운 accessToken을 요구한 상태");
            redisTemplate.delete(refreshToken); //두 사용자중 어느쪽이 부정한 접속자인지 알 수 없으니 둘다 로그아웃 처리
            throw new IllegalArgumentException("비정상적인 접속이 확인되어 로그아웃 됩니다.");
        }
    }

    /*
     refresh 토큰관련 의문점
     1. refresh토큰을 쓰게 되면서 서버에서 따로 토큰을 저장하는데 이럴바에는 session이 더 나은게 아닌가?
     2. 매번 refresh 토큰을 통해 access토큰을 새로 발급 받으려고 할때 refresh토큰도 새로 만들어서 저장한다고 하면,
      중복된 refresh 토큰을 가지고 있던 두 사용자중 먼저 발급 받는 쪽은 정상적으로 새로운 accesstoken과 refresh토큰을 발급 받지만,
      나중에 발급받는 쪽은 본인이 정당한 사용자더라도 새로 로그인이 필요한 상황이 된다.
      결과적으로 refresh 토큰이 탈취 당했을때의 대처가 되지 않게 된다.
    */

    public String validateTokens(HttpServletRequest req, HttpServletResponse res) {
        // accessToken 검증 실패 RefreshToken 검증 시작

        String refreshToken = getRefreshTokenFromHeader(req);

        if (refreshToken == null) {
            log.error("Refresh 토큰이 만료되었거나 Refresh 토큰이 존재하지 않습니다.");
            throw new RuntimeException("Refresh 토큰이 만료되었거나 Refresh 토큰이 존재하지 않습니다.");
        }
        log.info(refreshToken);
        validateRefreshToken(refreshToken);
        String newAccessToken = regenerateAccessToken(refreshToken);
        res.addHeader(JwtUtil.ACCESS_TOKEN, newAccessToken);
        res.addHeader(JwtUtil.REFRESH_TOKEN, refreshToken);

        log.info("토큰재발급 성공: {}", newAccessToken);
        return newAccessToken;
    }

    // 토큰에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getAccessTokenFromRedis(String refreshToken) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        return values.get(refreshToken);
    }
}
