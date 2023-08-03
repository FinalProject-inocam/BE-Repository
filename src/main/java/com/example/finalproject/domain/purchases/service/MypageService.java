package com.example.finalproject.domain.purchases.service;

import com.example.finalproject.auth.entity.User;
import com.example.finalproject.auth.repository.UserRepository;
import com.example.finalproject.domain.post.exception.PostsNotFoundException;
import com.example.finalproject.domain.purchases.dto.MypageRequestDto;
import com.example.finalproject.global.enums.SuccessCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.example.finalproject.global.enums.ErrorCode.NOT_FOUND_DATA;
import static com.example.finalproject.global.enums.ErrorCode.NO_AUTHORITY_TO_DATA;

@Service
@RequiredArgsConstructor
public class MypageService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public SuccessCode updateMypage(MypageRequestDto mypageRequestDto, User user) {
//        passwordEncoder.matches(mypageRequestDto.getPassword(), user.getPassword());
        if (passwordEncoder.matches(mypageRequestDto.getPassword(), user.getPassword())) {
            User newuser = userRepository.findById(user.getUserId()).orElseThrow(
                    () -> new PostsNotFoundException(NOT_FOUND_DATA)
            );
            newuser.update(mypageRequestDto);
        } else {
            throw new PostsNotFoundException(NO_AUTHORITY_TO_DATA);// 좀 있다 다른 파일 합치고 새로운 예외 추가해서 하는게 좋을듯?합니다
        }
        return SuccessCode.POST_CREATE_SUCCESS;// 나중에 깃 합치고 성공코드 수정하기!!
    }
}
