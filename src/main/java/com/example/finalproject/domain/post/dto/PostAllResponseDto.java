package com.example.finalproject.domain.post.dto;


import com.example.finalproject.domain.post.entity.Posts;
import lombok.Getter;



@Getter
public class PostAllResponseDto {
    Long post_id;
    String title;
    String content;
    Long comment_count;
    Long like_count;
    Boolean is_like;
    public PostAllResponseDto(Posts posts,Long comment_count,Long like_count,Boolean is_like) {
        this.content=posts.getContent();
        this.title=posts.getTitle();
        this.post_id=posts.getId();
        this.comment_count=comment_count;
        this.like_count=like_count;
        this.is_like=is_like;
    }
}
