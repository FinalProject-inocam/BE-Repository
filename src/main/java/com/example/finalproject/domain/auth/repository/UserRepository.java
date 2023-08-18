package com.example.finalproject.domain.auth.repository;

import com.example.finalproject.domain.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<User> findByEmail(String email);

    User findByNickname(String nickname);

    User findByKakaoId(Long kakaoId);

    User findByNaverId(String naverId);

    User findByGoogleId(String googleId);


//    Arrays findAllByUser(User user);
}
