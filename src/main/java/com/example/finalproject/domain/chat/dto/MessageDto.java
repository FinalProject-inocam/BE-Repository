package com.example.finalproject.domain.chat.dto;

import lombok.Getter;

@Getter
public class MessageDto {

    private Long roomId;
    private String nickname;
    private String content;

    // 기본 생성자, Getter, Setter, 기타 필요한 메서드들...
}