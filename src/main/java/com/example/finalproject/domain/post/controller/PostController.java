package com.example.finalproject.domain.post.controller;

import com.example.finalproject.auth.security.UserDetailsImpl;
import com.example.finalproject.domain.post.dto.PostAllResponseDto;
import com.example.finalproject.domain.post.dto.PostOneResponseDto;
import com.example.finalproject.domain.post.dto.PostRequestDto;
import com.example.finalproject.domain.post.service.PostsService;
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
    private final PostsService postsService;

    private String nickname="zin";
    @GetMapping
    public ApiResponse<?> getPost(){
        List<PostAllResponseDto> postResponseDtoList=postsService.getPost();
        return ResponseUtils.ok(postResponseDtoList);
    }

    @GetMapping("/{postId}")
    public ApiResponse<?> getOnePost(@PathVariable(name="postId") Long postid){
        PostOneResponseDto postOneResponseDto =postsService.getOnePost(postid);
        return ResponseUtils.ok(postOneResponseDto);
    }

    @PostMapping
    public ApiResponse<?> createPost(@RequestPart(value = "file", required = false) List<MultipartFile> multipartFile,
                                     @RequestPart(value = "data") PostRequestDto postRequestDto,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails)
    {
        SuccessCode successCode = postsService.createPost(postRequestDto, userDetails.getNickname(), multipartFile);
        return ResponseUtils.ok(successCode);
    }

        @PatchMapping("/{postId}")
        public ApiResponse<?> updatePost(@RequestPart(value = "file", required = false) List<MultipartFile> multipartFile,
                                         @RequestPart(value = "data") PostRequestDto postRequestDto,
                                         @PathVariable(name="postId") Long postid,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails)
        {
            SuccessCode successCode= postsService.updatePost(multipartFile,postRequestDto,postid,userDetails.getNickname());
            return ResponseUtils.ok(successCode);
        }

        @DeleteMapping("/{postId}")
        public ApiResponse<?> deletePost(@PathVariable(name="postId") Long postid,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails){
        SuccessCode successCode =postsService.deletePost(postid,userDetails.getNickname());
        return ResponseUtils.ok(successCode);
        }

    @PostMapping("/{postId}/like")
    public ApiResponse<?> likeNews(@PathVariable("postId") Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        SuccessCode successCode = postsService.likePost(postId,userDetails.getUser().getUserId());
        return ResponseUtils.ok(successCode);
    }
}
