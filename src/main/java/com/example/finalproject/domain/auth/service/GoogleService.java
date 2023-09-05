package com.example.finalproject.domain.auth.service;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.auth.repository.UserRepository;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.enums.UserRoleEnum;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.UUID;

import static com.example.finalproject.global.enums.SuccessCode.USER_LOGIN_SUCCESS;


@Service
@Slf4j(topic = "Google Login")
@RequiredArgsConstructor
public class GoogleService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${security.oauth2.google.client-id}")
    private String clientId;
    @Value("${security.oauth2.google.client-secret}")
    private String clientSecret;
    @Value("${security.oauth2.google.redirect-uri}")
    private String redirectUri;
    @Value("${security.oauth2.google.token-uri}")
    String tokenUri;
    @Value("${security.oauth2.google.resource-uri}")
    String resourceUri;

    public SuccessCode googleLogin(String code, HttpServletResponse response, HttpServletRequest request) throws IOException, ServletException {

        String googleAccessToken = getAccessToken(code);
        JsonNode userResourceNode = getUserResource(googleAccessToken);

        User googleUser = registerGoogleUserIfNeeded(userResourceNode);

        // 로그인 절차
        redisService.newLogin(googleUser, response, request);
        return USER_LOGIN_SUCCESS;
    }

    private String getAccessToken(String authorizationCode) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", authorizationCode);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity entity = new HttpEntity(params, headers);

        ResponseEntity<JsonNode> responseNode = restTemplate.exchange(tokenUri, HttpMethod.POST, entity, JsonNode.class);
        JsonNode accessTokenNode = responseNode.getBody();
        return accessTokenNode.get("access_token").asText();
    }

    private JsonNode getUserResource(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity entity = new HttpEntity(headers);
        return restTemplate.exchange(resourceUri, HttpMethod.GET, entity, JsonNode.class).getBody();
    }

    private User registerGoogleUserIfNeeded(JsonNode userResourceNode) {
        String email = userResourceNode.get("email").asText();
        String nickname = userResourceNode.get("name").asText();
        String googleId = userResourceNode.get("id").asText();
        String profile="https://finalimgbucket.s3.amazonaws.com/057c943e-27ba-4b0c-822d-e9637c2f2aff";
//                Long.parseLong(userResourceNode.get("googleId").asText());
        User googleUser = userRepository.findByGoogleId(googleId);

        if (googleUser == null) {
            // 카카오 사용자 email 동일한 email 가진 회원이 있는지 확인 // 이미 가입 email == kakao login email // @kakao, naver
            // 기존 회원가입을 kakaoEmail로 한경우
            String googleEmail = email;
            User sameEmailUser = userRepository.findByEmail(googleEmail).orElse(null);
            if (sameEmailUser != null) {
                googleUser = sameEmailUser; // kakaoId || default
                // 기존 회원정보에 카카오 Id 추가
                googleUser = googleUser.googleIdUpdate(googleId);
            } else {
                // 신규 회원가입
                // password: random UUID
                String password = UUID.randomUUID().toString(); // 랜덤, 사용자가 알 수 없게
                String encodedPassword = passwordEncoder.encode(password);
                String name = nickname;
                if (name.equals("E001") || name.equals("sever") || name.equals("date") || name.contains("!")) {
                    name = UUID.randomUUID().toString();
                }
                int num = 1;
                while (true) {
                    if (userRepository.existsByNickname(name)) {
                        name = name + String.valueOf(num);
                        num++;
                    } else {
                        break;
                    }
                }
                googleUser = new User(googleId, email, name, encodedPassword, UserRoleEnum.USER,profile);
            }

            userRepository.save(googleUser);
        }
        return googleUser;
    }
}