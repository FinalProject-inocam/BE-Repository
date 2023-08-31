package com.example.finalproject.domain.auth.service;

import com.example.finalproject.domain.auth.dto.NaverUserInfoDto;
import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.auth.repository.UserRepository;
import com.example.finalproject.global.enums.UserRoleEnum;
import com.example.finalproject.global.responsedto.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

import static com.example.finalproject.global.enums.SuccessCode.USER_LOGIN_SUCCESS;
import static com.example.finalproject.global.utils.ResponseUtils.ok;

@Slf4j(topic = "NAVER Login")
@Service
@RequiredArgsConstructor
public class NaverService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final RedisService redisService;

    @Value("${security.oauth2.naver.client-id}")
    private String naverClientId;

    @Value("${security.oauth2.naver.client-secret}")
    private String naverClientSecret;

    @Value("${security.oauth2.naver.resource-uri}")
    private String naverRedirectUri;

    public ApiResponse<?> naverLogin(String code, String state, HttpServletResponse response, HttpServletRequest request) throws JsonProcessingException {
        log.info("test: " + code);

        // 1. "인가 코드"로 "액세스 토큰" 요청
        String naverAccessToken = getToken(code, state);

        // 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
        JsonNode jsonNode = getNaverUserInfo(naverAccessToken);

        // 3. 필요시에 회원가입
        User naverUser = registerNaverUserIfNeeded(jsonNode);

        // 로그인 절차
        redisService.newLogin(naverUser, response, request);

        return ok(USER_LOGIN_SUCCESS);
    }

    private String getToken(String code, String state) throws JsonProcessingException {
        log.info("토큰가져오기");
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://nid.naver.com/oauth2.0/")
                .path("/token")
                .encode()
                .build()
                .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", naverClientId);
        body.add("client_secret", naverClientSecret);
        body.add("redirect_uri", naverRedirectUri);
        body.add("code", code);
        body.add("state", state);

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(body);

        // HTTP 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        return jsonNode.get("access_token").asText();
    }

    private JsonNode getNaverUserInfo(String accessToken) throws JsonProcessingException {
        log.info("토큰에서 유저정보 가져오기");
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com")
                .path("/v1/nid/me")
                .encode()
                .build()
                .toUri();
        log.info(accessToken);

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        log.info(headers.toString());

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(new LinkedMultiValueMap<>());

        // HTTP 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );
        log.info("요청 확인 전");
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        log.info(jsonNode.toString());
        log.info("요청 확인 후");
        return jsonNode;
    }

    private User registerNaverUserIfNeeded(JsonNode jsonNode) {
        log.info("미가입 회원 회원가입처리");
        String id = jsonNode.get("response").get("id").asText();

        // DB 에 중복된 naver Id 가 있는지 확인 // 이미 가입했는지 - 처음인지
        String nickname = jsonNode.get("response").get("nickname").asText();// 중복 nickname을 막기위해 고유 값인 userid를 추가로 붙여서 사용
        String email = jsonNode.get("response").get("email").asText();
        String naverId = id; // naver.com

        User naverUser = userRepository.findByNaverId(naverId);

        if (naverUser == null) {
            // 네이버 사용자 email 동일한 email 가진 회원이 있는지 확인 // 이미 가입 email == naver login email // @kakao, naver
            // 기존 회원가입을 naverEmail로 한경우
            String naverEmail = email;
            User sameEmailUser = userRepository.findByEmail(naverEmail).orElse(null);
            if (sameEmailUser != null) {
                naverUser = sameEmailUser; // kakaoId || default
                // 기존 회원정보에 네이버 Id 추가
                naverUser = naverUser.naverIdUpdate(naverId);
            } else {
                // 신규 회원가입
                // password: random UUID
                String password = UUID.randomUUID().toString(); // 랜덤, 사용자가 알 수 없게
                String encodedPassword = passwordEncoder.encode(password);

                String name = nickname;                // 닉네임이 중복 될 시 닉네임 옆에 숫자 붙여주기
                int num = 1;
                while (true) {
                    if (userRepository.existsByNickname(name)) {
                        name = nickname + String.valueOf(num);
                        num++;
                    } else {
                        break;
                    }
                }

                NaverUserInfoDto naverUserInfoDto = new NaverUserInfoDto(id, name, email);
                naverUser = new User(naverUserInfoDto, encodedPassword, UserRoleEnum.USER);
            }
            userRepository.save(naverUser);
        }
        return naverUser;
    }
}

