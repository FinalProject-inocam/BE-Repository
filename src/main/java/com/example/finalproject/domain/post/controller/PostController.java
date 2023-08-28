package com.example.finalproject.domain.post.controller;

import com.example.finalproject.domain.auth.security.UserDetailsImpl;
import com.example.finalproject.domain.post.dto.PostAllResponseDto;
import com.example.finalproject.domain.post.dto.PostListResponseDto;
import com.example.finalproject.domain.post.dto.PostOneResponseDto;
import com.example.finalproject.domain.post.dto.PostRequestDto;
import com.example.finalproject.domain.post.service.PostService;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.responsedto.ApiResponse;
import com.example.finalproject.global.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/communities")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping("/search")
    public ApiResponse<?> searchPosts(@RequestParam("page") int page,
                                      @RequestParam("size") int size,
                                      @RequestParam("keyword") String keyword,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Page<PostAllResponseDto> postAllResponseDtos = postService.searchPost(page, size, keyword, userDetails);
        return ResponseUtils.ok(postAllResponseDtos);
    }

    // 게시글 전체 조회
    @GetMapping
    public ApiResponse<?> getPosts(@RequestParam("category") String category,
                                   @RequestParam("page") int page,
                                   @RequestParam("size") int size,
                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Page<PostAllResponseDto> postResponseDtoList = postService.getPost(category, page, size, userDetails);
        return ResponseUtils.ok(postResponseDtoList);
    }

    // 게시글 상세 조회
    @GetMapping("/{postId}")
    public ApiResponse<?> getOnePost(@PathVariable(name = "postId") Long postId,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        PostOneResponseDto postOneResponseDto = postService.getOnePost(postId, userDetails);
        return ResponseUtils.ok(postOneResponseDto);
    }

    // 게시글 작성
    @PostMapping
    public ApiResponse<?> createPost(@RequestPart(value = "images", required = false) List<MultipartFile> multipartFile,
                                     @RequestPart(value = "data") PostRequestDto postRequestDto,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        SuccessCode successCode = postService.createPost(postRequestDto, userDetails.getUser(), multipartFile);
        return ResponseUtils.ok(successCode);
    }

    // 게시글 수정
    @PatchMapping("/{postId}")
    public ApiResponse<?> updatePost(@RequestPart(value = "images", required = false) List<MultipartFile> multipartFile,
                                     @RequestPart(value = "data") PostRequestDto postRequestDto,
                                     @PathVariable Long postId,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        SuccessCode successCode = postService.updatePost(multipartFile, postRequestDto, postId, userDetails.getNickname());
        return ResponseUtils.ok(successCode);
    }

    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ApiResponse<?> deletePost(@PathVariable(name = "postId") Long postId,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        SuccessCode successCode = postService.deletePost(postId, userDetails.getNickname());
        return ResponseUtils.ok(successCode);
    }

    // 게시글 좋아요
    @PatchMapping("/{postId}/like")
    public ApiResponse<?> likeNews(@PathVariable("postId") Long postId,
                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        SuccessCode successCode = postService.likePost(postId, userDetails.getUser().getUserId());
        return ResponseUtils.ok(successCode);
    }

    @GetMapping("/list")
    public ApiResponse<?> getPostsByTop() {
        PostListResponseDto postListResponseDto = postService.getPostByTop();
        return ResponseUtils.ok(postListResponseDto);
    }
}
