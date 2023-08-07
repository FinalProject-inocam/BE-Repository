package com.example.finalproject.domain.mail.repository;

import com.example.finalproject.domain.mail.entity.AuthCode;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface AuthCodeRepository extends JpaRepository<AuthCode,Long> {
    boolean existsByEmail(String to);

    AuthCode findByEmail(String to);
}
