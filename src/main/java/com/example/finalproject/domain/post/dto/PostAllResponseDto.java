package com.example.finalproject.domain.post.dto;

import com.example.finalproject.domain.post.entity.Post;
import lombok.Getter;


@Getter
public class PostAllResponseDto {
    Long post_id;
    String title;
    String content;
    Boolean is_like;
    Long like_count;
    Long comment_count;
    public PostAllResponseDto(Post post, Long comment_count, Long like_count, Boolean is_like) {
        this.content = post.getContent();
        this.title = post.getTitle();
        this.post_id = post.getId();
        this.comment_count = comment_count;
        this.like_count = like_count;
        this.is_like = is_like;
    }
}
