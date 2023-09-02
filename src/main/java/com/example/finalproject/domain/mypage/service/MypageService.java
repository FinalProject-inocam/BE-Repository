package com.example.finalproject.domain.mypage.service;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.auth.repository.UserRepository;
import com.example.finalproject.domain.auth.service.RedisService;
import com.example.finalproject.domain.mypage.dto.*;
import com.example.finalproject.domain.post.entity.Comment;
import com.example.finalproject.domain.post.entity.Post;
import com.example.finalproject.domain.post.entity.PostLike;
import com.example.finalproject.domain.post.repository.CommentRepository;
import com.example.finalproject.domain.post.repository.PostLikeRepository;
import com.example.finalproject.domain.post.repository.PostRepository;
import com.example.finalproject.domain.purchases.exception.PurchasesNotFoundException;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.responsedto.PageResponse;
import com.example.finalproject.global.utils.S3Utils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.example.finalproject.global.enums.ErrorCode.NO_AUTHORITY_TO_DATA;
import static com.example.finalproject.global.enums.ErrorCode.USER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class MypageService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;
    private final S3Utils s3Utils;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;

    @Transactional
    public SuccessCode updateMypage(MultipartFile multipartFile,
                                    MypageRequestDto mypageRequestDto,
                                    User user, HttpServletResponse response,
                                    HttpServletRequest request) {

        if (passwordEncoder.matches(mypageRequestDto.getPassword(), user.getPassword())) {
            User newuser = userRepository.findById(user.getUserId()).orElseThrow(
                    () -> new PurchasesNotFoundException(USER_NOT_FOUND)
            );

            String profileimg = s3Utils.updateProfile(multipartFile); // 프로필 사진 업데이트 후 URL을 가져옴

            String newpassword = passwordEncoder.encode(mypageRequestDto.getNewPassword());
            mypageRequestDto.setPasswordToNewPassword(passwordEncoder.encode(mypageRequestDto.getNewPassword()));
            newuser.update(mypageRequestDto, newpassword, profileimg);

            // 기존의 로그인 되어있던 모든계정에서 로그아웃 처리
            redisService.deleteAllRefreshToken(user.getNickname());
            // 새로운 user 정보로 로그인 처리
            redisService.newLogin(newuser, response, request);
        } else {
            throw new PurchasesNotFoundException(NO_AUTHORITY_TO_DATA);
        }
        return SuccessCode.MYPAGE_UPDATE_SUCCESS;
    }

    public MyPostPageDto getMyPost(User user, int size, int page){
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Post> postList=postRepository.findByUserUserId(user.getUserId(),pageable);
        long total = postList.getTotalElements();
        List<PostListDto> postListDtos = new ArrayList<>();
        for(Post post : postList){
            Long likeCount =postLikeRepository.countByPostId(post.getId());
            Long commentCount= Long.valueOf(post.getCommentList().size());
            PostListDto postListDto=new PostListDto(post,likeCount,commentCount);
            postListDtos.add(postListDto);
        }
        PageResponse pageResponse = new PageResponse<>(postListDtos, pageable, total);
        MyPostPageDto myPostPageDto = new MyPostPageDto(pageResponse);
     return myPostPageDto;
    }

    // 글 좋아요
    public MyPageDto getMylike(User user, int size, int page){
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<PostLike> postLike=postLikeRepository.findByUserUserId(user.getUserId(),pageable);
        long total = postLike.getTotalElements();
        List<ListDto> listDtos = new ArrayList<>();
        for(PostLike postlike : postLike){
            Long likeCount =postLikeRepository.countByPostId(postlike.getPost().getId());
            Long commentCount= Long.valueOf(postlike.getPost().getCommentList().size());
            ListDto postListDto=new ListDto(postlike.getPost(),likeCount,commentCount);
            listDtos.add(postListDto);
        }
        PageResponse pageResponse = new PageResponse<>(listDtos, pageable, total);
        MyPageDto myLikePageDto = new MyPageDto(pageResponse);
        return myLikePageDto;
    }

    // 댓글
    public MyPageDto getMyComment(User user, int size, int page) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Comment> mycomment=commentRepository.findByUserUserId(user.getUserId(), pageable);
        long total = mycomment.getTotalElements();
        List<ListDto> listDtos = new ArrayList<>();
        for(Comment comment : mycomment){
            Long likeCount =postLikeRepository.countByPostId(comment.getPost().getId());
            Long commentCount= Long.valueOf(comment.getPost().getCommentList().size());
            ListDto postListDto=new ListDto(comment.getPost(),likeCount,commentCount);
            listDtos.add(postListDto);
        }
        PageResponse pageResponse = new PageResponse<>(listDtos, pageable, total);
        MyPageDto myLikePageDto = new MyPageDto(pageResponse);
        return myLikePageDto;
    }

    public MypageResDto getMypage(User user) {
        return new MypageResDto(user);
    }


}