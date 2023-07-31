package com.example.finalproject.domain.post.dto;

import com.example.finalproject.domain.post.entity.Comments;
import com.example.finalproject.domain.post.entity.Image;
import com.example.finalproject.domain.post.entity.Posts;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PostOneResponseDto {
    private Long post_id;
    private String title;
    private String content;
    private List<String> imageUrls; // Image URL 목록을 저장
    private List<Comments> commentsList;
    public PostOneResponseDto(Posts post) {
        this.post_id= post.getId();
        this.title= post.getTitle();
        this.content= post.getContent();
        this.commentsList=post.getCommentList();
        this.imageUrls = post.getImageList().stream() // 이미지 URL만 추출
                .map(Image::getImage)
                .collect(Collectors.toList());
    }
}