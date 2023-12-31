package com.example.finalproject.domain.auth.security;

import com.example.finalproject.domain.auth.service.RedisService;
import com.example.finalproject.global.utils.ClientIpUtil;
import com.example.finalproject.global.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JWT Authorization")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final RedisService redisService;
    private final ClientIpUtil clientIpUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        // access 토큰 확인
        String accessTokenValueFromHeader = jwtUtil.getAccessTokenFromHeader(req);
        String refreshTokenValueFromHeader = jwtUtil.getRefreshTokenFromHeader(req);
        if (StringUtils.hasText(accessTokenValueFromHeader) || StringUtils.hasText(refreshTokenValueFromHeader)) {
            try {
                String accessTokenValue = "";
                if (jwtUtil.validateAccessToken(accessTokenValueFromHeader)) {
                    log.info("가지고 있던 accessToken이 유효함");
                    accessTokenValue = accessTokenValueFromHeader;
                } else {
                    log.info("가지고 있던 accessToken이 유효하지 않음");
                    jwtUtil.validateAccess(refreshTokenValueFromHeader, req);

                    String[] tokens = jwtUtil.validateTokens(req, res);

                    String newAccessToken = tokens[0];
                    accessTokenValue = jwtUtil.substringToken(newAccessToken);
                    String newRefreshToken = tokens[1];

                    // 잠시

                    String ipAddress = clientIpUtil.getClientIp(req);
                    redisService.setRefreshToken(newRefreshToken, ipAddress);
                }

                Claims info = jwtUtil.getUserInfoFromToken(accessTokenValue);


                setAuthentication(info.get("email", String.class));
                // 만일 토큰에 넣어주는 방식을 바꾸게 될경우 여기를 수정
            } catch (Exception e) {
                log.error(e.getMessage());
//                throw new AuthorizationServiceException(e.getMessage());
                // 여기서 걸릴만한 exception이... UsernameNotFoundException 밖에 안보이는데... 이거라도 잡으려면 해야하나...?
            }
        }

        filterChain.doFilter(req, res);
    }

    // 인증 처리
    public void setAuthentication(String email) {
        Authentication authentication = createAuthentication(email);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("인증처리: {}", (SecurityContextHolder.getContext().getAuthentication()));
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String email) {
        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
