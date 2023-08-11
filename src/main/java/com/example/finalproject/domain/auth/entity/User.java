package com.example.finalproject.domain.auth.entity;

import com.example.finalproject.domain.auth.dto.KakaoUserInfoDto;
import com.example.finalproject.domain.auth.dto.SignupRequestDto;
import com.example.finalproject.domain.mypage.dto.MypageRequestDto;
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

    @Column
    private String gender;

    @Column
    private String birthdate;

    @Column
    private String phoneNumber;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    @Column
    private Long kakaoId;

    @Column
    private String googleId;

    @Column
    private String profileImg;

    public User(SignupRequestDto requestDto, String password, UserRoleEnum role) {
        this.email = requestDto.getEmail();
        this.password = password;
        this.nickname = requestDto.getNickname();
        this.phoneNumber = requestDto.getPhoneNumber();
        this.gender = requestDto.getGender();
        this.birthdate = requestDto.getBirthdate();
        this.role = role;
    }

    public void update(MypageRequestDto mypageRequestDto, String newpassword, String profileImg) {
        this.password = newpassword;
        this.nickname = mypageRequestDto.getNickname();
        this.phoneNumber = mypageRequestDto.getPhoneNumber();
        this.profileImg = profileImg;
    }

    public User(KakaoUserInfoDto kakaoUserInfo, String password, UserRoleEnum role) {
        this.kakaoId = kakaoUserInfo.getId();
        this.email = kakaoUserInfo.getEmail();
        this.password = password;
        this.nickname = kakaoUserInfo.getNickname();
//        this.gender = kakaoUserInfo.getGender();
        this.role = role;
    }

    public User(String id, String email, String nickname, String password, UserRoleEnum role) {
        this.googleId = id;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
//        this.gender = kakaoUserInfo.getGender();
        this.role = role;
    }

    public User kakaoIdUpdate(Long kakaoId) {
        this.kakaoId = kakaoId;
        return this;
    }

    public User googleIdUpdate(String id) {
        this.googleId = id;
        return this;
    }
}
