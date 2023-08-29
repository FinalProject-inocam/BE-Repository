package com.example.finalproject.domain.post.dto;

import com.example.finalproject.domain.post.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostListDto {
    private Integer rank;
    private Long postId;
    private String title;
    private LocalDateTime createdAt;
    private String url;

    public PostListDto(Post post, Integer rank) {
        this.rank = rank;
        this.postId = post.getId();
        this.title = post.getTitle();
        this.createdAt = post.getCreatedAt();
        this.url = "community/review/" + post.getId();
    }
}
