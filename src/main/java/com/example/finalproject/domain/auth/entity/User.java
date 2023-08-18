package com.example.finalproject.domain.auth.entity;

import com.example.finalproject.domain.auth.dto.KakaoUserInfoDto;
import com.example.finalproject.domain.auth.dto.NaverUserInfoDto;
import com.example.finalproject.domain.auth.dto.SignupRequestDto;
import com.example.finalproject.domain.mypage.dto.MypageRequestDto;
import com.example.finalproject.global.enums.UserGenderEnum;
import com.example.finalproject.global.enums.UserRoleEnum;
import com.example.finalproject.global.utils.Timestamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "user")
public class User extends Timestamped {
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
    @Enumerated(value = EnumType.STRING)
    private UserGenderEnum gender;

    @Column
    private Integer birthYear;

    @Column
    private String phoneNumber;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    @Column
    private Long kakaoId;

    @Column
    private String naverId;

    @Column
    private String googleId;

    @Column
    private String profileImg;

    public User(SignupRequestDto requestDto, String password, UserRoleEnum role, UserGenderEnum gender, String IMAGE_URL) {
        this.email = requestDto.getEmail();
        this.password = password;
        this.nickname = requestDto.getNickname();
        this.phoneNumber = requestDto.getPhoneNumber();
        this.gender = gender;
        this.birthYear = requestDto.getBirthYear();
        this.role = role;
        this.profileImg = IMAGE_URL;
    }

    public void update(MypageRequestDto mypageRequestDto, String newpassword, String profileImg) {
        this.password = newpassword;
        this.nickname = mypageRequestDto.getNickname();
        this.phoneNumber = mypageRequestDto.getPhoneNumber();
        this.profileImg = profileImg;
    }

    // 카카오
    public User(KakaoUserInfoDto kakaoUserInfo, String password, UserRoleEnum role) {
        this.kakaoId = kakaoUserInfo.getId();
        this.email = kakaoUserInfo.getEmail();
        this.password = password;
        this.nickname = kakaoUserInfo.getNickname();
        this.role = role;
    }

    // 구글
    public User(String googleId, String email, String nickname, String password, UserRoleEnum role) {
        this.googleId = googleId;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
    }

    // 네이버
    public User(NaverUserInfoDto naverUserInfoDto, String password, UserRoleEnum role) {
        this.naverId = naverUserInfoDto.getId();
        this.email = naverUserInfoDto.getEmail();
        this.password = password;
        this.nickname = naverUserInfoDto.getNickname();
        this.role = role;
    }


    // 카카오
    public User kakaoIdUpdate(Long kakaoId) {
        this.kakaoId = kakaoId;
        return this;
    }

    // 구글
    public User googleIdUpdate(String googleId) {
        this.googleId = googleId;
        return this;
    }

    // 네이버
    public User naverIdUpdate(String naverId) {
        this.naverId = naverId;
        return this;
    }
}
