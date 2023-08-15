package com.example.finalproject.domain.mypage.service;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.auth.repository.UserRepository;
import com.example.finalproject.domain.auth.service.RedisService;
import com.example.finalproject.domain.mypage.dto.MypageRequestDto;
import com.example.finalproject.domain.mypage.dto.MypageResDto;
import com.example.finalproject.domain.purchases.exception.PurchasesNotFoundException;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.responsedto.ApiResponse;
import com.example.finalproject.global.utils.S3Utils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.example.finalproject.global.enums.ErrorCode.NO_AUTHORITY_TO_DATA;
import static com.example.finalproject.global.enums.ErrorCode.USER_NOT_FOUND;
import static com.example.finalproject.global.utils.ResponseUtils.ok;

@Slf4j
@Service
@RequiredArgsConstructor
public class MypageService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;
    private final S3Utils s3Utils;

    @Transactional
    public SuccessCode updateMypage(MultipartFile multipartFile, MypageRequestDto mypageRequestDto, User user, HttpServletResponse response) {

        if (passwordEncoder.matches(mypageRequestDto.getPassword(), user.getPassword())) {
            User newuser = userRepository.findById(user.getUserId()).orElseThrow(
                    () -> new PurchasesNotFoundException(USER_NOT_FOUND)
            );

            String profileimg = s3Utils.updateProfile(multipartFile); // 프로필 사진 업데이트 후 URL을 가져옴
//            profileimg = user.getProfileImg(); // 새로운 프로필 사진 URL을 설정

            String newpassword = passwordEncoder.encode(mypageRequestDto.getNewPassword());
            mypageRequestDto.setPasswordToNewPassword(passwordEncoder.encode(mypageRequestDto.getNewPassword()));
            newuser.update(mypageRequestDto, newpassword, profileimg);

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

    public ApiResponse<MypageResDto> getMypage(User user) {
        return ok(new MypageResDto(user));
    }
}