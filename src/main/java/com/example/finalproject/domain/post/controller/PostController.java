package com.example.finalproject.domain.post.controller;

import com.example.finalproject.domain.auth.security.UserDetailsImpl;
import com.example.finalproject.domain.post.dto.PostAllResponseDto;
import com.example.finalproject.domain.post.dto.PostLikeRequestDto;
import com.example.finalproject.domain.post.dto.PostOneResponseDto;
import com.example.finalproject.domain.post.dto.PostRequestDto;
import com.example.finalproject.domain.post.service.PostService;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.responsedto.ApiResponse;
import com.example.finalproject.global.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping
    public ApiResponse<?> getPosts(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<PostAllResponseDto> postResponseDtoList = postService.getPost(userDetails);
        return ResponseUtils.ok(postResponseDtoList);
    }

    @GetMapping("/{postId}")
    public ApiResponse<?> getOnePost(@PathVariable(name = "postId") Long postId,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        PostOneResponseDto postOneResponseDto = postService.getOnePost(postId,userDetails);
        return ResponseUtils.ok(postOneResponseDto);
    }

    @PostMapping
    public ApiResponse<?> createPost(@RequestPart(value = "images", required = false) List<MultipartFile> multipartFile,
                                     @RequestPart(value = "data") PostRequestDto postRequestDto,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        SuccessCode successCode = postService.createPost(postRequestDto, userDetails.getUser(), multipartFile);
        return ResponseUtils.ok(successCode);
    }

    @PatchMapping("/{postId}")
    public ApiResponse<?> updatePost(@RequestPart(value = "images", required = false) List<MultipartFile> multipartFile,
                                     @RequestPart(value = "data") PostRequestDto postRequestDto,
                                     @PathVariable Long postId,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        SuccessCode successCode = postService.updatePost(multipartFile, postRequestDto, postId, userDetails.getNickname());
        return ResponseUtils.ok(successCode);
    }

    @DeleteMapping("/{postId}")
    public ApiResponse<?> deletePost(@PathVariable(name = "postId") Long postId,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        SuccessCode successCode = postService.deletePost(postId, userDetails.getNickname());
        return ResponseUtils.ok(successCode);
    }

    @PostMapping("/{postId}/like")
    public ApiResponse<?> likeNews(@PathVariable("postId") Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody PostLikeRequestDto postLikeRequestDto) {
        SuccessCode successCode = postService.likePost(postId, userDetails.getUser().getUserId(),postLikeRequestDto);
        return ResponseUtils.ok(successCode);
    }
}
