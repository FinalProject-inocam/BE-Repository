package com.example.finalproject.domain.mypage.dto;

import com.example.finalproject.domain.post.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostListDto {
    private Long id;
    private Long likeCount;
    private Long commentCount;
    private String title;
    private LocalDateTime createdAt;

    public PostListDto(Post post, Long likeCount, Long commentCount) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.commentCount = commentCount;
        this.likeCount = likeCount;
        this.createdAt = post.getCreatedAt();
    }
}
