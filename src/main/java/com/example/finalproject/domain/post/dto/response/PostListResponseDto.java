package com.example.finalproject.domain.post.dto.response;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class PostListResponseDto {
    private List<PostListDto> likeList;
    private List<PostListDto> recentList;
    private String newImgUrl;

    public PostListResponseDto(List<PostListDto> likeList, List<PostListDto> recentList, String imgUrl) {
        this.likeList = likeList;
        this.recentList = recentList;
        this.newImgUrl= imgUrl;
    }
}
