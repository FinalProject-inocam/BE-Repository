package com.example.finalproject.domain.mail.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
public class AuthCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Email
    private String email;

    private String authcode;

    public AuthCode(String email,String authcode){
        this.email=email;
        this.authcode=authcode;
    }
}
