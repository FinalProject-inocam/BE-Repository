package com.example.finalproject.domain.post.controller;

import com.example.finalproject.domain.auth.security.UserDetailsImpl;
import com.example.finalproject.domain.post.dto.request.CommentRequestDto;
import com.example.finalproject.domain.post.service.CommentService;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.responsedto.ApiResponse;
import com.example.finalproject.global.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/communities/{postId}/comments")
public class CommentController {
    private final CommentService commentsService;

    // 댓글 생성
    @PostMapping
    public ApiResponse<?> createComments(@PathVariable(name = "postId") Long postId,
                                         @RequestBody CommentRequestDto commentRequestDto,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        SuccessCode successCode = commentsService.createComment(postId, commentRequestDto, userDetails.getNickname());
        return ResponseUtils.ok(successCode);
    }

    // 댓글 수정
    @PatchMapping("/{commentId}")
    public ApiResponse<?> updateComments(@PathVariable(name = "postId") Long postId,
                                         @PathVariable(name = "commentId") Long commentId,
                                         @RequestBody CommentRequestDto commentRequestDto,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        SuccessCode successCode = commentsService.updateComment(postId, commentId, commentRequestDto, userDetails.getNickname());
        return ResponseUtils.ok(successCode);
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ApiResponse<?> deleteComments(@PathVariable(name = "postId") Long postId,
                                         @PathVariable(name = "commentId") Long commentId,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        SuccessCode successCode = commentsService.deleteComment(postId, commentId, userDetails.getNickname());
        return ResponseUtils.ok((successCode));
    }

    // 댓글 좋아요
    @PatchMapping("/{commentId}/like")
    public ApiResponse<?> likeComment(@PathVariable("commentId") Long commentId,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        SuccessCode successCode = commentsService.likeComment(commentId, userDetails.getUser().getUserId());
        return ResponseUtils.ok(successCode);
    }
}
