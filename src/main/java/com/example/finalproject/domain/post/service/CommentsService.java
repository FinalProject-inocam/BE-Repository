package com.example.finalproject.domain.post.service;

import com.example.finalproject.auth.entity.User;
import com.example.finalproject.auth.repository.UserRepository;
import com.example.finalproject.domain.post.dto.CommentRequestDto;
import com.example.finalproject.domain.post.entity.CommentLike;
import com.example.finalproject.domain.post.entity.Comments;
import com.example.finalproject.domain.post.entity.PostLike;
import com.example.finalproject.domain.post.entity.Posts;
import com.example.finalproject.domain.post.exception.PostsNotFoundException;
import com.example.finalproject.domain.post.repository.CommentLikeRepository;
import com.example.finalproject.domain.post.repository.PostCommentsRepository;
import com.example.finalproject.domain.post.repository.PostsRepository;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.enums.UserRoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.example.finalproject.global.enums.ErrorCode.*;
import static com.example.finalproject.global.enums.SuccessCode.LIKE_CANCEL;
import static com.example.finalproject.global.enums.SuccessCode.LIKE_SUCCESS;

@Service
@RequiredArgsConstructor
public class CommentsService {
    private final PostCommentsRepository postCommentsRepository;
    private final PostsRepository postsRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;
    @Transactional
    public SuccessCode createService(Long postId,CommentRequestDto commentRequestDto,String nickname) {
        Posts post =postsRepository.findById(postId).orElseThrow(
                () -> new PostsNotFoundException(NOT_FOUND_DATA)
        );
        List<Comments> commentsList=post.getCommentList();
        User user = userRepository.findByNickname(nickname);
        Comments comment = new Comments(nickname,post,commentRequestDto.getComment(),user);
        commentsList.add(comment);
        postCommentsRepository.save(comment);

        return SuccessCode.COMMENT_CREATE_SUCCESS;
    }
    @Transactional
    public SuccessCode updateComment(Long postId, Long commentId, CommentRequestDto commentRequestDto,String nickname){
        Comments comments =validateAuthority(commentId,nickname);
        comments.setComment(commentRequestDto.getComment());
        return SuccessCode.COMMENT_UPDATE_SUCCESS;
    }


    public SuccessCode deleteComments(Long postId, Long commentId,String nickname) {
        Comments comments =validateAuthority(commentId,nickname);
        postCommentsRepository.delete(comments);
        return SuccessCode.COMMENT_DELETE_SUCCESS;
    }

    public SuccessCode likePost(Long commentId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new PostsNotFoundException(NOT_FOUND_CLIENT)
        );
        Comments comments=postCommentsRepository.findById(commentId).orElseThrow(
                () -> new PostsNotFoundException(NOT_FOUND_DATA)
        );

        Optional<CommentLike> commentLike = commentLikeRepository.findByCommentsIdAndUserUserId(commentId,userId);
        if(commentLike.isPresent()){
            commentLikeRepository.delete(commentLike.get());
            return LIKE_CANCEL;
        }else{
            commentLikeRepository.save(new CommentLike(user, comments));
            return LIKE_SUCCESS;
        }
    }

    private Comments validateAuthority(Long id, String nickname) {
        Comments comments = postCommentsRepository.findById(id).orElseThrow(
                () -> new PostsNotFoundException(NOT_FOUND_DATA)
        );

        User user = userRepository.findByNickname(nickname);

        if (!user.getRole().equals(UserRoleEnum.ADMIN) && !comments.getUser().getUserId().equals(user.getUserId())) {
            throw new PostsNotFoundException(NO_AUTHORITY_TO_DATA);
        }

        return comments;
    }
}
