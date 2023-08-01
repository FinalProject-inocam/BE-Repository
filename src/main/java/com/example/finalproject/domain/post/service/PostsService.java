package com.example.finalproject.domain.post.service;

import com.example.finalproject.auth.entity.User;
import com.example.finalproject.auth.repository.UserRepository;
import com.example.finalproject.auth.security.UserDetailsImpl;
import com.example.finalproject.domain.post.dto.CommentResponseDto;
import com.example.finalproject.domain.post.dto.PostAllResponseDto;
import com.example.finalproject.domain.post.dto.PostOneResponseDto;
import com.example.finalproject.domain.post.dto.PostRequestDto;
import com.example.finalproject.domain.post.entity.Comments;
import com.example.finalproject.domain.post.entity.Image;
import com.example.finalproject.domain.post.entity.PostLike;
import com.example.finalproject.domain.post.entity.Posts;
import com.example.finalproject.domain.post.exception.PostsNotFoundException;
import com.example.finalproject.domain.post.repository.PostImageRepository;
import com.example.finalproject.domain.post.repository.PostLikeRepository;
import com.example.finalproject.domain.post.repository.PostsRepository;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.enums.UserRoleEnum;
import com.example.finalproject.global.utils.S3Utils;
import lombok.RequiredArgsConstructor;
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
public class PostsService {
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostImageRepository postImageRepository;
    private final PostsRepository postsRepository;
    private final S3Utils s3Utils;
    @Transactional
    public List<PostAllResponseDto> getPost(UserDetailsImpl userDetails) {
        List<Posts> posts= postsRepository.findAll();
        List<PostAllResponseDto> postsList=new ArrayList<>();
        Boolean is_like;
        if(userDetails==null){
            is_like=false;
        }
        else{
            is_like=postLikeRepository.existsByUserUserId(userDetails.getUser().getUserId());
        }
        for(Posts post : posts) {
            Long comment_count= Long.valueOf(post.getCommentList().size());
            Long like_count=postLikeRepository.countByPostsId(post.getId());

            PostAllResponseDto postAllResponseDto = new PostAllResponseDto(post,comment_count,like_count,is_like);
            postsList.add(postAllResponseDto);
        }
        return postsList;
    }


    @Transactional
    public PostOneResponseDto getOnePost(Long postid) {
        Posts post =postsRepository.findById(postid).orElseThrow(
                () -> new PostsNotFoundException(NOT_FOUND_DATA)
        );
        List<CommentResponseDto> commentResponseDtoList=new ArrayList<>();
        List<Comments> commentList=post.getCommentList();
        for (Comments cmt : commentList){
            CommentResponseDto commentResponseDto=new CommentResponseDto(cmt);
            commentResponseDtoList.add(commentResponseDto);
        }
        PostOneResponseDto postOneResponseDto=new PostOneResponseDto(post,commentResponseDtoList);
        return postOneResponseDto;
    }

    @Transactional
    public SuccessCode createPost(PostRequestDto postRequestDto, String nickname, List<MultipartFile> multipartFile) {
        User user=userRepository.findByNickname(nickname);
        Posts posts=new Posts(postRequestDto,nickname,user);

        if (!(multipartFile == null)) {
            List<String> imageUrls =s3Utils.uploadFile(multipartFile);
            for (String imageUrl : imageUrls) {
                Image postImage =new Image(imageUrl);
                posts.getImageList().add(postImage);
            }
        }
        for(Image image : posts.getImageList()){
            postImageRepository.save(image);
        }
        Posts postsave=postsRepository.save(posts);
        return POST_CREATE_SUCCESS;
    }

    @Transactional
    public SuccessCode updatePost(List<MultipartFile> multipartFile, PostRequestDto postRequestDto, Long postid,String nickname) {
        Posts post =validateAuthority(postid,nickname);
        List<Image> images = new ArrayList<>(post.getImageList()); // 복사본 사용
        for(Image image : images){
            post.getImageList().remove(image); // 연관관계 끊기
            postImageRepository.delete(image);
        }
        if (!(multipartFile == null)) {
            List<String> imageUrls =s3Utils.uploadFile(multipartFile);
            for (String imageUrl : imageUrls) {
                Image postImage =new Image(imageUrl);
                post.getImageList().add(postImage);
            }
        }

        for(Image image : post.getImageList()){
            postImageRepository.save(image);
        }
        post.setTitle(postRequestDto.getTitle());
        post.setContent(postRequestDto.getContent());
        return POST_UPDATE_SUCCESS;
    }
    @Transactional
    public SuccessCode deletePost(Long postid,String nickname) {
        Posts post =validateAuthority(postid,nickname);
        List<Image> images = new ArrayList<>(post.getImageList()); // 복사본 사용
        for(Image image : images){
            post.getImageList().remove(image); // 연관관계 끊기
            postImageRepository.delete(image);
        }
        postsRepository.delete(post);

        return POST_DELETE_SUCCESS;
    }
    @Transactional
    public SuccessCode likePost(Long postId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new PostsNotFoundException(NOT_FOUND_CLIENT)
        );
        Posts posts=postsRepository.findById(postId).orElseThrow(
                () -> new PostsNotFoundException(NOT_FOUND_DATA)
        );
        Optional<PostLike> postLike = postLikeRepository.findByPostsIdAndUserUserId(postId,userId);
        if(postLike.isPresent()){
            postLikeRepository.delete(postLike.get());
            return LIKE_CANCEL;
        }else{
            postLikeRepository.save(new PostLike(user, posts));
            return LIKE_SUCCESS;
        }
    }

    private Posts validateAuthority(Long id, String nickname) {
        Posts post = postsRepository.findById(id).orElseThrow(
                () -> new PostsNotFoundException(NOT_FOUND_CLIENT)
        );

        User user = userRepository.findByNickname(nickname);

        if (!user.getRole().equals(UserRoleEnum.ADMIN) && !post.getUser().getUserId().equals(user.getUserId())) {
            throw new PostsNotFoundException(NO_AUTHORITY_TO_DATA);
        }

        return post;
    }

}
