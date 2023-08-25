package com.example.finalproject.domain.mail.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class AuthCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Email
    private String email;

    private String authcode;

    LocalDateTime expirationTime;

    public AuthCode(String email, String authcode, LocalDateTime expirationTime) {
        this.email = email;
        this.authcode = authcode;
        this.expirationTime = expirationTime;
    }

    public void update(String code) {
        this.authcode = code;
    }
}
