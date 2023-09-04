package com.example.finalproject.domain.post.dto.response;

import com.example.finalproject.domain.post.entity.Post;
import com.example.finalproject.global.enums.UserRoleEnum;
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
    private Boolean isAdmin;

    public PostAllResponseDto(Post post, Long comment_count, Long like_count, Boolean is_like) {
        this.content = post.getContent();
        this.title = post.getTitle();
        this.postId = post.getId();
        this.category = post.getCategory();
        this.commentCount = comment_count;
        this.likeCount = like_count;
        this.isLike = is_like;
        if (post.getImageList().size() == 0) {
            this.thumbnail = "https://finalimgbucket.s3.amazonaws.com/057c943e-27ba-4b0c-822d-e9637c2f2aff";
        } else {
            this.thumbnail = post.getImageList().get(0).getImage();
        }
        this.createAt = post.getCreatedAt();
        this.nickname = post.getNickname();
        this.view = post.getView();
        this.url = "community/review/" + post.getId();
        this.isAdmin = post.getUser().getRole() == UserRoleEnum.ADMIN;
    }
}
