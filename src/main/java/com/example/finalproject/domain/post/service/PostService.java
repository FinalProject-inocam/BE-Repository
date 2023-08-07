package com.example.finalproject.domain.post.service;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.auth.repository.UserRepository;
import com.example.finalproject.domain.auth.security.UserDetailsImpl;
import com.example.finalproject.domain.post.dto.*;
import com.example.finalproject.domain.post.entity.Comments;
import com.example.finalproject.domain.post.entity.Image;
import com.example.finalproject.domain.post.entity.Post;
import com.example.finalproject.domain.post.entity.PostLike;
import com.example.finalproject.domain.post.exception.PostsNotFoundException;
import com.example.finalproject.domain.post.repository.PostImageRepository;
import com.example.finalproject.domain.post.repository.PostLikeRepository;
import com.example.finalproject.domain.post.repository.PostRepository;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.enums.UserRoleEnum;
import com.example.finalproject.global.utils.S3Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.finalproject.global.enums.ErrorCode.*;
import static com.example.finalproject.global.enums.SuccessCode.*;

@Service
@RequiredArgsConstructor
public class PostService {
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostImageRepository postImageRepository;
    private final PostRepository postRepository;
    private final S3Utils s3Utils;

    @Transactional
    public List<PostAllResponseDto> getPost(UserDetailsImpl userDetails) {
        List<Post> posts = postRepository.findAll();
        List<PostAllResponseDto> postsList = new ArrayList<>();
        for (Post post : posts) {
            Boolean is_like;
            if (userDetails == null) {
                is_like = false;
            } else {
                is_like = postLikeRepository.existsByPostIdAndUserUserId(post.getId(),userDetails.getUser().getUserId());
            }
            Long comment_count = Long.valueOf(post.getCommentList().size());
            Long like_count = postLikeRepository.countByPostId(post.getId());

            PostAllResponseDto postAllResponseDto = new PostAllResponseDto(post, comment_count, like_count, is_like);
            postsList.add(postAllResponseDto);
        }
        return postsList;
    }

    @Transactional
    public PostOneResponseDto getOnePost(Long postid,UserDetailsImpl userDetails) {
        Post post = postRepository.findById(postid).orElseThrow(
                () -> new PostsNotFoundException(NOT_FOUND_DATA)
        );
        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
        List<Comments> commentList = post.getCommentList();
        for (Comments cmt : commentList) {
            CommentResponseDto commentResponseDto = new CommentResponseDto(cmt);
            commentResponseDtoList.add(commentResponseDto);
        }
        Long like_count = postLikeRepository.countByPostId(post.getId());
        Boolean is_like;
        if (userDetails == null) {
            is_like = false;
        } else {
            is_like = postLikeRepository.existsByPostIdAndUserUserId(post.getId(),userDetails.getUser().getUserId());
        }

        PostOneResponseDto postOneResponseDto = new PostOneResponseDto(post, commentResponseDtoList,like_count,is_like);
        return postOneResponseDto;
    }

    @Transactional
    public SuccessCode createPost(PostRequestDto postRequestDto, User user, List<MultipartFile> multipartFile) {
        Post post = new Post(postRequestDto, user);

        validateMultipartFile(multipartFile, post);

        for (Image image : post.getImageList()) {
            postImageRepository.save(image);
        }
        postRepository.save(post);
        return POST_CREATE_SUCCESS;
    }


    @Transactional
    public SuccessCode updatePost(List<MultipartFile> multipartFile, PostRequestDto postRequestDto, Long postId, String nickname) {
        Post post = validateAuthority(postId, nickname);
        List<Image> images = new ArrayList<>(post.getImageList()); // 복사본 사용
        for (Image image : images) {
            post.getImageList().remove(image); // 연관관계 끊기
            postImageRepository.delete(image);
        }
        validateMultipartFile(multipartFile, post);

        for (Image image : post.getImageList()) {
            postImageRepository.save(image);
        }
        post.setTitle(postRequestDto.getTitle());
        post.setContent(postRequestDto.getContent());
        return POST_UPDATE_SUCCESS;
    }

    @Transactional
    public SuccessCode deletePost(Long postId, String nickname) {
        Post post = validateAuthority(postId, nickname);
        List<Image> images = new ArrayList<>(post.getImageList()); // 복사본 사용
        for (Image image : images) {
            post.getImageList().remove(image); // 연관관계 끊기
            postImageRepository.delete(image);
        }
        User user=userRepository.findByNickname(nickname);
        postLikeRepository.deleteByPostIdAndUserUserId(postId,user.getUserId()).orElseThrow(null);

        postRepository.delete(post);

        return POST_DELETE_SUCCESS;
    }

    private void validateMultipartFile(List<MultipartFile> multipartFile, Post post) {
        if (!(multipartFile == null)) {
            List<String> imageUrls = s3Utils.uploadFile(multipartFile);
            for (String imageUrl : imageUrls) {
                Image postImage = new Image(imageUrl);
                post.getImageList().add(postImage);
            }
        }
    }

    @Transactional
    public SuccessCode likePost(Long postId, Long userId, PostLikeRequestDto postLikeRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new PostsNotFoundException(NOT_FOUND_CLIENT)
        );
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new PostsNotFoundException(NOT_FOUND_DATA)
        );
        Optional<PostLike> postLike = postLikeRepository.findByPostIdAndUserUserId(postId, userId);
        if (postLike.isPresent()) {
            postLikeRepository.delete(postLike.get());
            return LIKE_CANCEL;
        } else {
            postLikeRepository.save(new PostLike(user, post));
            return LIKE_SUCCESS;
        }
    }

    private Post validateAuthority(Long Id, String nickname) {
        Post post = postRepository.findById(Id).orElseThrow(
                () -> new PostsNotFoundException(NOT_FOUND_CLIENT));

        User user = userRepository.findByNickname(nickname);

        if (!user.getRole().equals(UserRoleEnum.ADMIN) && !post.getUser().getUserId().equals(user.getUserId())) {
            throw new PostsNotFoundException(NO_AUTHORITY_TO_DATA);
        }

        return post;
    }

}
