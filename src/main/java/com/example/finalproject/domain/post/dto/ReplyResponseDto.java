package com.example.finalproject.domain.post.dto;

import com.example.finalproject.domain.post.entity.Reply;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReplyResponseDto {
    private Long replyId;
    private String nickname;
    private String reply;
    private LocalDateTime createAt;
    private LocalDateTime modifiedAt;
    private Boolean isLike;
    private Long likeCount;

    public ReplyResponseDto(Reply reply, Long likeCount, Boolean isLike) {
        this.replyId = reply.getId();
        this.reply = reply.getReply();
        this.nickname = reply.getNickname();
        this.createAt = reply.getCreatedAt();
        this.modifiedAt = reply.getModifiedAt();
        this.likeCount = likeCount;
        this.isLike = isLike;
    }
}
