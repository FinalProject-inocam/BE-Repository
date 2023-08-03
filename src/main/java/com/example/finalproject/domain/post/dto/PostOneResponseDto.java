package com.example.finalproject.domain.post.dto;

import com.example.finalproject.domain.post.entity.Image;
import com.example.finalproject.domain.post.entity.Post;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PostOneResponseDto {
    private Long post_id;
    private String title;
    private String content;
    private List<String> imageUrls; // Image URL 목록을 저장
    private List<CommentResponseDto> commentsList;

    public PostOneResponseDto(Post post, List<CommentResponseDto> commentResponseDtoList) {
        this.post_id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.commentsList = commentResponseDtoList;
        this.imageUrls = post.getImageList().stream() // 이미지 URL만 추출
                .map(Image::getImage)
                .collect(Collectors.toList());
    }
}
