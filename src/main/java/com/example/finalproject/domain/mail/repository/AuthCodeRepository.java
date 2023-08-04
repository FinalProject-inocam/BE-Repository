package com.example.finalproject.domain.mail.repository;

import com.example.finalproject.domain.mail.entity.AuthCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthCodeRepository extends JpaRepository<AuthCode,Long> {
}
