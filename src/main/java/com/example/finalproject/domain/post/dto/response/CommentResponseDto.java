package com.example.finalproject.domain.post.dto.response;

import com.example.finalproject.domain.post.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CommentResponseDto {
    private Long commentId;
    private String nickname;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Long likeCount;
    private Boolean isLike;
    private List<ReplyResponseDto> replyList = new ArrayList<>();

    public CommentResponseDto(Comment cmt,Long likeCount,Boolean isLike,List<ReplyResponseDto> replyList) {
        this.commentId = cmt.getId();
        this.comment = cmt.getComment();
        this.createdAt = cmt.getCreatedAt();
        this.modifiedAt = cmt.getModifiedAt();
        this.nickname = cmt.getNickname();
        this.likeCount=likeCount;
        this.isLike=isLike;
        this.replyList=replyList;
    }
}
