package com.example.finalproject.domain.post.dto.response;

import lombok.Getter;

@Getter
public class PostIdDto {
    Long postId;
    public PostIdDto(Long postId){
        this.postId=postId;
    }
}
