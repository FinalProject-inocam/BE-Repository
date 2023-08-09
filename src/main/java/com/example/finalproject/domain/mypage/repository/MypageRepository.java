package com.example.finalproject.domain.mypage.repository;

import com.example.finalproject.domain.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MypageRepository extends JpaRepository<User, Long> {
}
