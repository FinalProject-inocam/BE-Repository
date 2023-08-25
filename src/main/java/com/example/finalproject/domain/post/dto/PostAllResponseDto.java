package com.example.finalproject.domain.post.dto;

import com.example.finalproject.domain.post.entity.Post;
import lombok.Getter;

@Getter
public class PostAllResponseDto {
    private Long postId;
    private String title;
    private String content;
    private Boolean isLike;
    private Long likeCount;
    private Long commentCount;

    public PostAllResponseDto(Post post, Long comment_count, Long like_count, Boolean is_like) {
        this.content = post.getContent();
        this.title = post.getTitle();
        this.postId = post.getId();
        this.commentCount = comment_count;
        this.likeCount = like_count;
        this.isLike = is_like;
    }
}
