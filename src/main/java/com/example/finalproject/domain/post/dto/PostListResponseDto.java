package com.example.finalproject.domain.post.dto;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class PostListResponseDto {
    private Map<Integer, Object> likeList;
    private Map<Integer, Object> recentList;
    private String imgUrl;

    public PostListResponseDto(Map<Integer, Object> likeList, Map<Integer, Object> recentList, String imgUrl) {
        this.likeList = likeList;
        this.recentList = recentList;
        this.imgUrl= imgUrl;
    }
}