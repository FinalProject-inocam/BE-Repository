package com.example.finalproject.domain.post.dto;

import com.example.finalproject.domain.post.entity.Comments;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {
    private Long comment_id;
    private String nickname;
    private String comment;
    private LocalDateTime create_at;
    private LocalDateTime modified_at;

    public CommentResponseDto(Comments cmt) {
        this.comment_id = cmt.getId();
        this.comment = cmt.getComment();
        this.create_at = cmt.getCreatedAt();
        this.modified_at = cmt.getModifiedAt();
        this.nickname = cmt.getNickname();
    }
}
