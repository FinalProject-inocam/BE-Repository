package com.example.finalproject.domain.post.controller;

import com.example.finalproject.domain.post.dto.CommentRequestDto;
import com.example.finalproject.domain.post.entity.Comments;
import com.example.finalproject.domain.post.service.CommentsService;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.responsedto.ApiResponse;
import com.example.finalproject.global.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentsController {
    private final CommentsService commentsService;
    String nickname="zin";
    @PostMapping("/posts/{postId}/comments")
    public ApiResponse<?> createComments(
            @PathVariable(name="postid") Long postid,
            @RequestBody CommentRequestDto commentRequestDto){
        SuccessCode successCode=commentsService.createService(postid,commentRequestDto,this.nickname);
        return ResponseUtils.ok(successCode);
    }
}
