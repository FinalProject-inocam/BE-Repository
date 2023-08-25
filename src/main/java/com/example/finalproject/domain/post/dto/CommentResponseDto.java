package com.example.finalproject.domain.post.dto;

import com.example.finalproject.domain.post.entity.Comment;
import com.example.finalproject.domain.post.entity.Reply;
import lombok.Getter;
import org.springframework.data.redis.support.collections.RedisList;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CommentResponseDto {
    private Long commentId;
    private String nickname;
    private String comment;
    private LocalDateTime createAt;
    private LocalDateTime modifiedAt;
    private Long likeCount;
    private Boolean isLike;
    private List<ReplyResponseDto> replyList = new ArrayList<>();

    public CommentResponseDto(Comment cmt,Long likeCount,Boolean isLike,List<ReplyResponseDto> replyList) {
        this.commentId = cmt.getId();
        this.comment = cmt.getComment();
        this.createAt = cmt.getCreatedAt();
        this.modifiedAt = cmt.getModifiedAt();
        this.nickname = cmt.getNickname();
        this.likeCount=likeCount;
        this.isLike=isLike;
        this.replyList=replyList;
    }
}
