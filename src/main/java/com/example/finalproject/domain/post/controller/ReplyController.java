package com.example.finalproject.domain.post.controller;

import com.example.finalproject.domain.auth.security.UserDetailsImpl;
import com.example.finalproject.domain.post.dto.CommentRequestDto;
import com.example.finalproject.domain.post.service.ReplyService;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.responsedto.ApiResponse;
import com.example.finalproject.global.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/communities/{postId}/comments/{commentId}/replies")
@RequiredArgsConstructor
public class ReplyController {
    private final ReplyService replyService;

    // 대댓글 작성
    @PostMapping
    public ApiResponse<?> createComments(@PathVariable(name = "commentId") Long commentId,
                                         @RequestBody CommentRequestDto commentRequestDto,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        SuccessCode successCode = replyService.createReply(commentId, commentRequestDto, userDetails.getUser());
        return ResponseUtils.ok(successCode);
    }

    // 대댓글 수정
    @PatchMapping("/{replyId}")
    public ApiResponse<?> updateComments(@PathVariable(name = "replyId") Long replyId,
                                         @RequestBody CommentRequestDto commentRequestDto,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        SuccessCode successCode = replyService.updateReply(replyId, commentRequestDto, userDetails.getUser());
        return ResponseUtils.ok(successCode);
    }

    // 대댓글 삭제
    @DeleteMapping("/{replyId}")
    public ApiResponse<?> deleteComments(@PathVariable(name = "replyId") Long replyId,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        SuccessCode successCode = replyService.deleteReply(replyId, userDetails.getUser());
        return ResponseUtils.ok((successCode));
    }

    // 대댓글 좋아요
    @PatchMapping("{replyId}/like")
    public ApiResponse<?> likeReply(@PathVariable("replyId") Long replyId,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        SuccessCode successCode = replyService.likeReply(replyId, userDetails.getUser().getUserId());
        return ResponseUtils.ok(successCode);
    }
}
