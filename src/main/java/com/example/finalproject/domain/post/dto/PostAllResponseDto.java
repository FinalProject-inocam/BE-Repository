package com.example.finalproject.domain.post.dto;

import com.example.finalproject.domain.post.entity.Post;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostAllResponseDto {
    private Long postId;
    private String title;
    private String content;
    private Boolean isLike;
    private Long likeCount;
    private Long commentCount;
    private String category;
    private Long view;
    private String thumbnail;
    private LocalDateTime createAt;
    private String nickname;
    private String url;

    public PostAllResponseDto(Post post, Long comment_count, Long like_count, Boolean is_like) {
        this.content = post.getContent();
        this.title = post.getTitle();
        this.postId = post.getId();
        this.category = post.getCategory();
        this.commentCount = comment_count;
        this.likeCount = like_count;
        this.isLike = is_like;
        if (post.getImageList().size() == 0) {
            this.thumbnail = "https://finalimgbucket.s3.amazonaws.com/2945e31e-d47d-4c41-9da8-eae9e695fa50";
        } else {
            this.thumbnail = post.getImageList().get(0).getImage();
        }
        this.createAt = post.getCreatedAt();
        this.nickname = post.getNickname();
        this.view = post.getView();
    }
}
