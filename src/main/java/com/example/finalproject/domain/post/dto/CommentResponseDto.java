package com.example.finalproject.domain.post.dto;

import com.example.finalproject.domain.post.entity.Comments;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {
    private Long id;
    private String comment;
    private String nickname;
    private LocalDateTime createAt;
    private LocalDateTime modifiedAt;
//    private Posts posts;

    public CommentResponseDto(Comments cmt) {
        this.id= cmt.getId();
        this.comment=cmt.getComment();
        this.createAt=cmt.getCreatedat();
        this.modifiedAt=cmt.getModifiedat();
        this.nickname=cmt.getNickname();
    }
}
