package com.example.finalproject.domain.auth.entity;

import com.example.finalproject.domain.auth.dto.SignupRequestDto;
import com.example.finalproject.domain.purchases.dto.MypageRequestDto;
import com.example.finalproject.global.enums.UserRoleEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String email; // 사용자 이름

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private String birthdate;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    public User(SignupRequestDto requestDto, String password, UserRoleEnum role) {
        this.email = requestDto.getEmail();
        this.password = password;
        this.nickname = requestDto.getNickname();
        this.phoneNumber = requestDto.getPhoneNumber();
        this.gender = requestDto.getGender();
        this.birthdate = requestDto.getBirthdate();
        this.role = role;
    }

    public void update(MypageRequestDto mypageRequestDto,String newpassword) {
        this.password = newpassword;
        this.nickname = mypageRequestDto.getNickname();
        this.phoneNumber = mypageRequestDto.getPhoneNumber();
    }
}
