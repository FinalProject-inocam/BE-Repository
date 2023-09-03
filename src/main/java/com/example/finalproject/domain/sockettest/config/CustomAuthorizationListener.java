package com.example.finalproject.domain.sockettest.config;

import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.HandshakeData;
import com.example.finalproject.global.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

//@RequiredArgsConstructor
//@Slf4j
//public class CustomAuthorizationListener implements AuthorizationListener {
//
//    private final JwtUtil jwtUtil;
//
//    @Override
//    public boolean isAuthorized(HandshakeData data) {
//        String accessToken = data.getHttpHeaders().get("Authorization");
//        String refreshToken = data.getHttpHeaders().get("Refresh");
//
//        if (StringUtils.hasText(accessToken) || StringUtils.hasText(refreshToken)) {
//            try {
//                String accessTokenValue = "";
//                if (jwtUtil.validateAccessToken(accessToken)) {
//                    log.info("가지고 있던 accessToken이 유효함");
//                    accessTokenValue = accessToken;
//                } else {
//                    log.info("가지고 있던 accessToken이 유효하지 않음");
//                    jwtUtil.validateAccess(refreshToken, req);
//
//                    String[] tokens = jwtUtil.validateTokens(req, res);
//
//                    String newAccessToken = tokens[0];
//                    accessTokenValue = jwtUtil.substringToken(newAccessToken);
//                    String newRefreshToken = tokens[1];
//
//                    // 잠시
//
//                    String ipAddress = clientIpUtil.getClientIp(req);
//                    redisService.setRefreshToken(newRefreshToken, ipAddress);
//                }
//
//                Claims info = jwtUtil.getUserInfoFromToken(accessTokenValue);
//
//
//                setAuthentication(info.get("email", String.class));
//                // 만일 토큰에 넣어주는 방식을 바꾸게 될경우 여기를 수정
//            } catch (Exception e) {
//                log.error(e.getMessage());
////                throw new AuthorizationServiceException(e.getMessage());
//                // 여기서 걸릴만한 exception이... UsernameNotFoundException 밖에 안보이는데... 이거라도 잡으려면 해야하나...?
//            }
//        }
//
//        // 여기에서 사용자를 인증하는 로직을 구현합니다.
//        // 인증에 성공하면 true를 반환하고, 그렇지 않으면 false를 반환합니다.
//
//        // 예제: 사용자 토큰을 확인하고 유효한지 검증
//        String token = data.getSingleUrlParam("token");
//        return isValidToken(token);
//    }
//}
