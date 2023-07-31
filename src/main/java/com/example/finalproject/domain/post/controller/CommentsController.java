package com.example.finalproject.domain.post.controller;

import com.example.finalproject.domain.post.dto.CommentRequestDto;
import com.example.finalproject.domain.post.service.CommentsService;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.responsedto.ApiResponse;
import com.example.finalproject.global.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/{postId}/comments")
public class CommentsController {
    private final CommentsService commentsService;
    String nickname="zin";
    @PostMapping
    public ApiResponse<?> createComments(
            @PathVariable(name="postId") Long postId,
            @RequestBody CommentRequestDto commentRequestDto){
        SuccessCode successCode=commentsService.createService(postId,commentRequestDto,this.nickname);
        return ResponseUtils.ok(successCode);
    }
    @PatchMapping("/{commentId}")
    public ApiResponse<?> updateComments(@PathVariable(name="postId")Long postId,
                                        @PathVariable(name="commentId")Long commentId,
                                         @RequestBody CommentRequestDto commentRequestDto
    ){
        SuccessCode successCode=commentsService.updateComment(postId,commentId,commentRequestDto);
        return ResponseUtils.ok(successCode);
    }

    @DeleteMapping("/{commentId}")
    public ApiResponse<?> deleteComments(@PathVariable(name="postId")Long postId,
                                         @PathVariable(name="commentId")Long commentId){
        SuccessCode successCode=commentsService.deleteComments(postId,commentId);
        return ResponseUtils.ok((successCode));
    }
}
