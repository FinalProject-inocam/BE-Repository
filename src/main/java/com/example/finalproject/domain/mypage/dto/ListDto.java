package com.example.finalproject.domain.mypage.dto;

import com.example.finalproject.domain.post.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ListDto {
    private Long id;
    private Long likeCount;
    private Long commentCount;
    private String title;
    private String nickname;
    private LocalDateTime createdAt;

    public ListDto(Post post, Long likeCount, Long commentCount) {
        this.id = post.getId();
        this.createdAt = post.getCreatedAt();
        this.title = post.getTitle();
        this.nickname = post.getNickname();
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }
}
