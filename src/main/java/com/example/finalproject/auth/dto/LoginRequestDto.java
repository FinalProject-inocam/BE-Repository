package com.example.finalproject.auth.dto;

import lombok.Getter;

@Getter
public class LoginRequestDto {
    private String email;
    private String password;
}