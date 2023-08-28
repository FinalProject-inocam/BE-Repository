package com.example.finalproject.domain.post.dto;

import com.example.finalproject.domain.post.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostListDto {
    private Long id;
    private String title;
    private LocalDateTime createdAt;

    public PostListDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.createdAt = post.getCreatedAt();
    }
}
