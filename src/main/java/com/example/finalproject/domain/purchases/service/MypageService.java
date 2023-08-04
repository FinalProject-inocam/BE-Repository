package com.example.finalproject.domain.purchases.service;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.auth.repository.UserRepository;
import com.example.finalproject.domain.post.exception.PostsNotFoundException;
import com.example.finalproject.domain.purchases.dto.MypageRequestDto;
import com.example.finalproject.global.enums.SuccessCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.finalproject.global.enums.ErrorCode.NOT_FOUND_DATA;
import static com.example.finalproject.global.enums.ErrorCode.NO_AUTHORITY_TO_DATA;

@Slf4j
@Service
@RequiredArgsConstructor
public class MypageService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SuccessCode updateMypage(MypageRequestDto mypageRequestDto, User user) {

        if (passwordEncoder.matches(mypageRequestDto.getPassword(), user.getPassword())) {
            User newuser = userRepository.findById(user.getUserId()).orElseThrow(
                    () -> new PostsNotFoundException(NOT_FOUND_DATA)
            );

            String newpassword = passwordEncoder.encode(mypageRequestDto.getNewPassword());
            mypageRequestDto.setPasswordToNewPassword(passwordEncoder.encode(mypageRequestDto.getNewPassword()));
            newuser.update(mypageRequestDto, newpassword);
        } else {
            throw new PostsNotFoundException(NO_AUTHORITY_TO_DATA);
        }
        return SuccessCode.POST_CREATE_SUCCESS;
    }
}
