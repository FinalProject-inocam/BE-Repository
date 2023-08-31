package com.example.finalproject.global.enums;

import lombok.Getter;


@Getter
public enum UserGenderEnum {
    MALE("MALE"),  // 남성
    FEMALE("FEMALE"), // 여성
    COMPANY("COMPANY");  // 회사

    private final String gender;

//    UserGenderEnum(String gender) {
//        this.gender = gender;
//    }
//
//    public String getGender() {
//        return this.gender;
//    }

    UserGenderEnum(String gender){
        this.gender = gender;
    }

//    public static class Gender {
//        public static final String MALE = "male";
//        public static final String FEMALE = "female";
//    }
}