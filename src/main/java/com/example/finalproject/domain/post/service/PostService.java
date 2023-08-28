package com.example.finalproject.domain.post.service;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.auth.repository.UserRepository;
import com.example.finalproject.domain.auth.security.UserDetailsImpl;
import com.example.finalproject.domain.post.dto.*;
import com.example.finalproject.domain.post.entity.*;
import com.example.finalproject.domain.post.exception.PostsNotFoundException;
import com.example.finalproject.domain.post.repository.*;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.enums.UserRoleEnum;
import com.example.finalproject.global.responsedto.PageResponse;
import com.example.finalproject.global.utils.S3Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static com.example.finalproject.global.enums.ErrorCode.*;
import static com.example.finalproject.global.enums.SuccessCode.*;

@Slf4j(topic = "postService")
@Service
@RequiredArgsConstructor
public class PostService {
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostImageRepository postImageRepository;
    private final PostRepository postRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final ReplyLikeRepository replyLikeRepository;
    private final S3Utils s3Utils;

    @Transactional
    public PostPageDto getPost(String category, int page, int size, UserDetailsImpl userDetails) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Post> postPage = postRepository.findByCategory(category, pageable);

        List<PostAllResponseDto> postsList = new ArrayList<>();

        if (category.equals("total")) {
            postPage = postRepository.findAll(pageable);
            if (page == 1) {
                Post notificationPost = postRepository.findByCategoryOrderByCreatedAtDesc("notification").orElseThrow(
                        () -> new PostsNotFoundException(NOT_FOUND_DATA)
                );
                Boolean is_like = getaBoolean(userDetails, notificationPost);
                Long comment_count = Long.valueOf(notificationPost.getCommentList().size());
                Long like_count = postLikeRepository.countByPostId(notificationPost.getId());
                PostAllResponseDto postAllResponseDto = new PostAllResponseDto(notificationPost, comment_count, like_count, is_like);
                postsList.add(postAllResponseDto);
            }
        }
        long total = postPage.getTotalElements();

        for (Post post : postPage) {
            Boolean is_like = getaBoolean(userDetails, post);
            Long comment_count = Long.valueOf(post.getCommentList().size());
            Long like_count = postLikeRepository.countByPostId(post.getId());
            PostAllResponseDto postAllResponseDto = new PostAllResponseDto(post, comment_count, like_count, is_like);
            postsList.add(postAllResponseDto);
        }
        PageResponse pageResponse = new PageResponse<>(postsList, pageable, total);
        return new PostPageDto(pageResponse);
    }

    public SearchPageDto searchPost(int page, int size, String keyword, UserDetailsImpl userDetails) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));
        log.info("keyword : " + keyword);

        if (keyword.isBlank()) {
            throw new PostsNotFoundException(NOT_FOUND_KEYWORD);
        }
        Page<Post> searchList = postRepository.searchByTitle(keyword, pageable);
        List<PostAllResponseDto> postsList = new ArrayList<>();
        long total = searchList.getTotalElements();

        for (Post post : searchList) {
            Boolean is_like = getaBoolean(userDetails, post);
            Long comment_count = Long.valueOf(post.getCommentList().size());
            Long like_count = postLikeRepository.countByPostId(post.getId());
            PostAllResponseDto postAllResponseDto = new PostAllResponseDto(post, comment_count, like_count, is_like);
            postsList.add(postAllResponseDto);
        }
        log.info("keyword : " + keyword);
        PageResponse pageResponse = new PageResponse<>(postsList, pageable, total);
        return new SearchPageDto(pageResponse);
    }

    @Transactional
    public PostListResponseDto getPostByTop() {

        List<PostListDto> recentList = postRepository.findTop5ByOrderByCreatedAtDesc().stream()
                .map(PostListDto::new).toList();
        Map<Integer, Object> recentListMap = new HashMap<>();

        for (int i = 0; i < recentList.size(); i++) {
            recentListMap.put(i + 1, recentList.get(i));
        }

        List<PostListDto> likeList = postRepository.findTop5ByOrderByPostLikesSizeDesc().stream()
                .map(PostListDto::new).toList();
        Map<Integer, Object> likeListMap = new HashMap<>();

        for (int i = 0; i < likeList.size(); i++) {
            likeListMap.put(i + 1, likeList.get(i));
        }

        String imgUrl = "https://finalimgbucket.s3.amazonaws.com/2945e31e-d47d-4c41-9da8-eae9e695fa50";

        PostListResponseDto postListResponseDto = new PostListResponseDto(likeListMap, recentListMap, imgUrl);

        return postListResponseDto;
    }

    @Transactional
    public PostOneResponseDto getOnePost(Long postid, UserDetailsImpl userDetails) {
        Post post = postRepository.findById(postid).orElseThrow(
                () -> new PostsNotFoundException(NOT_FOUND_DATA)
        );

        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
        List<Comment> commentList = post.getCommentList();

        // 게시글 조회수 증가
        post.viewCount();

        for (Comment cmt : commentList) {
            List<ReplyResponseDto> replyList = new ArrayList<>();
            for (Reply reply : cmt.getReplyList()) {
                Long replyLikeCount = replyLikeRepository.countByReplyId(reply.getId());
                Boolean isLikeReply = getreplyBoolean(userDetails, reply);
                ReplyResponseDto responseDto = new ReplyResponseDto(reply, replyLikeCount, isLikeReply);
                replyList.add(responseDto);
            }
            Long like_count = commentLikeRepository.countByCommentId(cmt.getId());
            Boolean is_like = getcmtBoolean(userDetails, cmt);
            CommentResponseDto commentResponseDto = new CommentResponseDto(cmt, like_count, is_like, replyList);
            commentResponseDtoList.add(commentResponseDto);
        }
        Long like_count = postLikeRepository.countByPostId(post.getId());
        Boolean is_like = getaBoolean(userDetails, post);

        PostOneResponseDto postOneResponseDto = new PostOneResponseDto(post, commentResponseDtoList, like_count, is_like);
        return postOneResponseDto;
    }

    @Transactional
    public SuccessCode createPost(PostRequestDto postRequestDto, User user, List<MultipartFile> multipartFile) {
        Post post = new Post(postRequestDto, user);
        validateMultipartFile(multipartFile, post);
        savePost(post);
        postRepository.save(post);
        return POST_CREATE_SUCCESS;
    }

    @Transactional
    public SuccessCode updatePost(List<MultipartFile> multipartFile, PostRequestDto postRequestDto, Long postId, String nickname) {
        Post post = validateAuthority(postId, nickname);
        List<Image> images = new ArrayList<>(post.getImageList()); // 복사본 사용
        imgeUpdate(post, images);
        validateMultipartFile(multipartFile, post);
        savePost(post);
        post.setTitle(postRequestDto.getTitle());
        post.setContent(postRequestDto.getContent());
        return POST_UPDATE_SUCCESS;
    }

    @Transactional
    public SuccessCode deletePost(Long postId, String nickname) {
        Post post = validateAuthority(postId, nickname);
        List<Image> images = new ArrayList<>(post.getImageList()); // 복사본 사용
        imgeUpdate(post, images);
        User user = userRepository.findByNickname(nickname);
        postLikeRepository.deleteByPostIdAndUserUserId(postId, user.getUserId()).orElseThrow(null);
        postRepository.delete(post);
        return POST_DELETE_SUCCESS;
    }

    @Transactional
    public SuccessCode likePost(Long postId, Long userId) {
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

    // 좋아요 선택?
    private Boolean getaBoolean(UserDetailsImpl userDetails, Post post) {
        Boolean is_like;
        if (userDetails == null) {
            is_like = false;
        } else {
            is_like = postLikeRepository.existsByPostIdAndUserUserId(post.getId(), userDetails.getUser().getUserId());
        }
        return is_like;
    }

    private Boolean getcmtBoolean(UserDetailsImpl userDetails, Comment comment) {
        Boolean is_like;
        if (userDetails == null) {
            is_like = false;
        } else {
            is_like = commentLikeRepository.existsByCommentIdAndUserUserId(comment.getId(), userDetails.getUser().getUserId());
        }
        return is_like;
    }

    private Boolean getreplyBoolean(UserDetailsImpl userDetails, Reply reply) {
        Boolean is_like;
        if (userDetails == null) {
            is_like = false;
        } else {
            is_like = replyLikeRepository.existsByReplyIdAndUserUserId(reply.getId(), userDetails.getUser().getUserId());
        }
        return is_like;
    }

    // 이미지 올리기
    private void imgeUpdate(Post post, List<Image> images) {
        for (Image image : images) {
            post.getImageList().remove(image); // 연관관계 끊기
            postImageRepository.delete(image);
        }
    }

    private void validateMultipartFile(List<MultipartFile> multipartFile, Post post) {
        if (s3Utils.isFile(multipartFile)) {
            List<String> imageUrls = s3Utils.uploadFile(multipartFile);
            for (String imageUrl : imageUrls) {
                Image postImage = new Image(imageUrl);
                post.getImageList().add(postImage);
            }
        }
    }

    // 포스트 저장하기
    private void savePost(Post post) {
        for (Image image : post.getImageList()) {
            postImageRepository.save(image);
        }
    }
}
