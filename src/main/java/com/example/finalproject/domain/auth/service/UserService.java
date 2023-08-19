package com.example.finalproject.domain.auth.service;

import com.example.finalproject.domain.auth.dto.SignupRequestDto;
import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.auth.repository.UserRepository;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.enums.UserGenderEnum;
import com.example.finalproject.global.enums.UserRoleEnum;
import com.example.finalproject.global.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j(topic = "user service")
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate redisTemplate;
    private final JwtUtil jwtUtil;

    @Value("${admin.token}") // Base64 Encode 한 SecretKey
    private String ADMIN_TOKEN;

    public SuccessCode signup(SignupRequestDto requestDto) {
        String email = requestDto.getEmail();
        String password = passwordEncoder.encode(requestDto.getPassword());
        String nickname = requestDto.getNickname();
        // 이메일 중복확인
        if (validateEmail(email)) {
            log.error("이메일 중복");
            throw new IllegalArgumentException("중복되는 이메일이 있습니다.");
        }
        // 닉네임 중복확인
        if (validateNickname(nickname)) {
            log.error("닉네임 중복");
            throw new IllegalArgumentException("중복되는 닉네임이 있습니다.");
        }
        // 사용자 ROLE 확인
        UserRoleEnum role = UserRoleEnum.USER;
        if (requestDto.isAdmin()) {
            if (!ADMIN_TOKEN.equals(requestDto.getAdminToken())) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = UserRoleEnum.ADMIN;
        }
        UserGenderEnum genderEnum = null;
        if (requestDto.getGender().equals("male")){
            genderEnum = UserGenderEnum.MALE;
        }
        if (requestDto.getGender().equals("female")) {
            genderEnum = UserGenderEnum.FEMALE;
        }

        String IMAGE_URL = "https://finalimgbucket.s3.ap-northeast-2.amazonaws.com/63db46a0-b705-4af5-9e39-6cb56bbfe842";
        User user = new User(requestDto, password, role, genderEnum, IMAGE_URL);
        userRepository.save(user);
        return SuccessCode.USER_SIGNUP_SUCCESS;
    }

    public SuccessCode logout(HttpServletRequest request) {
        String refreshToken = request.getHeader(jwtUtil.REFRESH_TOKEN);
        redisTemplate.delete(refreshToken);
        return SuccessCode.USER_LOGOUT_SUCCESS;
    }

    public SuccessCode checkEmail(String email) {
        if (validateEmail(email)) {
            return SuccessCode.USER_CHECK_EMAIL_FALSE;
        }
        return SuccessCode.USER_CHECK_EMAIL_TRUE;
    }

    public SuccessCode checkNickname(String nickname) {
        if (validateNickname(nickname)) {
            return SuccessCode.USER_CHECK_NICKNAME_FALSE;
        }
        return SuccessCode.USER_CHECK_NICKNAME_TRUE;
    }

    private Boolean validateEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private Boolean validateNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }
}
