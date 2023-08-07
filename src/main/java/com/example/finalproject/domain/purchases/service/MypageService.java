package com.example.finalproject.domain.purchases.service;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.auth.repository.UserRepository;
import com.example.finalproject.domain.auth.service.RedisService;
import com.example.finalproject.domain.purchases.dto.MypageRequestDto;
import com.example.finalproject.domain.purchases.exception.PurchasesNotFoundException;
import com.example.finalproject.global.enums.SuccessCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.finalproject.global.enums.ErrorCode.NO_AUTHORITY_TO_DATA;
import static com.example.finalproject.global.enums.ErrorCode.USER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class MypageService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;

    @Transactional
    public SuccessCode updateMypage(MypageRequestDto mypageRequestDto, User user, HttpServletResponse response) {

        if (passwordEncoder.matches(mypageRequestDto.getPassword(), user.getPassword())) {
            User newuser = userRepository.findById(user.getUserId()).orElseThrow(
                    () -> new PurchasesNotFoundException(USER_NOT_FOUND)
            );

            String newpassword = passwordEncoder.encode(mypageRequestDto.getNewPassword());
            mypageRequestDto.setPasswordToNewPassword(passwordEncoder.encode(mypageRequestDto.getNewPassword()));
            newuser.update(mypageRequestDto, newpassword);

            // 기존의 로그인 되어있던 모든계정에서 로그아웃 처리
            redisService.deleteAllRefreshToken(user.getNickname());
            // 새로운 user 정보로 로그인 처리
            redisService.newLogin(newuser, response);

        } else {
            throw new PurchasesNotFoundException(NO_AUTHORITY_TO_DATA);
        }

//        String nickname = requestDto.getNickname();
//        // 닉네임 중복확인
//        if (checkEmail(nickname)) {
//            log.error("닉네임 중복");
//            throw new IllegalArgumentException("중복되는 닉네임이 있습니다.");
//        }
        return SuccessCode.MYPAGE_UPDATE_SUCCESS;
    }
}
