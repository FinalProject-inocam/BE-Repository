package com.example.finalproject.domain.post.dto;

import com.example.finalproject.domain.post.entity.Post;
import lombok.Getter;


@Getter
public class PostAllResponseDto {
    Long postId;
    String title;
    String content;
    Boolean isLike;
    Long likeCount;
    Long commentCount;

    public PostAllResponseDto(Post post, Long comment_count, Long like_count, Boolean is_like) {
        this.content = post.getContent();
        this.title = post.getTitle();
        this.postId = post.getId();
        this.commentCount = comment_count;
        this.likeCount = like_count;
        this.isLike = is_like;
    }
}
