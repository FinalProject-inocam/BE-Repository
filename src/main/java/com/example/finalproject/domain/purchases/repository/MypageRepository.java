package com.example.finalproject.domain.purchases.repository;

import com.example.finalproject.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MypageRepository extends JpaRepository<User, Long> {
}
