package com.example.finalproject.domain.post.dto;


import com.example.finalproject.domain.post.entity.Posts;
import lombok.Getter;

import java.util.List;

@Getter
public class PostAllResponseDto {
    Long post_id;
    String title;
    String content;
    Long comment_count;
    public PostAllResponseDto(Posts posts,Long comment_count) {
        this.content=posts.getContent();
        this.title=posts.getTitle();
        this.post_id=posts.getId();
        this.comment_count=comment_count;
    }
}
