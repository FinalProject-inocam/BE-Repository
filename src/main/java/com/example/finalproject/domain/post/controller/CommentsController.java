package com.example.finalproject.domain.post.controller;

import com.example.finalproject.domain.auth.security.UserDetailsImpl;
import com.example.finalproject.domain.post.dto.CommentRequestDto;
import com.example.finalproject.domain.post.service.CommentsService;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.responsedto.ApiResponse;
import com.example.finalproject.global.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/{postId}/comments")
public class CommentsController {
    private final CommentsService commentsService;
    String nickname = "zin";

    @PostMapping
    public ApiResponse<?> createComments(
            @PathVariable(name = "postId") Long postId,
            @RequestBody CommentRequestDto commentRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        SuccessCode successCode = commentsService.createService(postId, commentRequestDto, userDetails.getNickname());
        return ResponseUtils.ok(successCode);
    }

    @PatchMapping("/{commentId}")
    public ApiResponse<?> updateComments(@PathVariable(name = "postId") Long postId,
                                         @PathVariable(name = "commentId") Long commentId,
                                         @RequestBody CommentRequestDto commentRequestDto,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        SuccessCode successCode = commentsService.updateComment(postId, commentId, commentRequestDto, userDetails.getNickname());
        return ResponseUtils.ok(successCode);
    }

    @DeleteMapping("/{commentId}")
    public ApiResponse<?> deleteComments(@PathVariable(name = "postId") Long postId,
                                         @PathVariable(name = "commentId") Long commentId,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        SuccessCode successCode = commentsService.deleteComments(postId, commentId, userDetails.getNickname());
        return ResponseUtils.ok((successCode));
    }

    @PostMapping("/{commentId}/like")
    public ApiResponse<?> likeComment(@PathVariable("commentId") Long commentId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        SuccessCode successCode = commentsService.likePost(commentId, userDetails.getUser().getUserId());
        return ResponseUtils.ok(successCode);
    }
}
