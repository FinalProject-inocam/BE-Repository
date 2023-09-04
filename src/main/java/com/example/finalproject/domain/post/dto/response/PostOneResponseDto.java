package com.example.finalproject.domain.post.dto.response;

import com.example.finalproject.domain.post.entity.Image;
import com.example.finalproject.domain.post.entity.Post;
import com.example.finalproject.global.enums.UserRoleEnum;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PostOneResponseDto {
    private Long postId;
    private String title;
    private String content;
    private String nickname;
    private Boolean isLike;
    private Long likeCount;
    private LocalDateTime createAt;
    private List<String> imageUrls; // Image URL 목록을 저장
    private List<CommentResponseDto> commentsList;
    private Boolean isAdmin;

    public PostOneResponseDto(Post post, List<CommentResponseDto> commentResponseDtoList, Long like_count, Boolean is_like) {
        this.postId = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.nickname = post.getNickname();
        this.commentsList = commentResponseDtoList;
        this.imageUrls = post.getImageList().stream() // 이미지 URL만 추출
                .map(Image::getImage)
                .collect(Collectors.toList());
        this.likeCount = like_count;
        this.isLike = is_like;
        this.createAt = post.getCreatedAt();
        this.isAdmin = post.getUser().getRole() == UserRoleEnum.ADMIN;
    }
}


