package com.example.finalproject.global.utils;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.auth.security.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j(topic = "guestUtil")
@Component
public class GuestUtil {
    public User checkGuest(UserDetailsImpl userDetails) {
        User user = null;
        try {
            user = userDetails.getUser();
        } catch (NullPointerException e) {
            log.info("게스트 사용자 입니다.");
        }
        return user;
    }
}
