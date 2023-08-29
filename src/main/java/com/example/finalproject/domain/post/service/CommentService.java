package com.example.finalproject.domain.post.service;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.auth.repository.UserRepository;
import com.example.finalproject.domain.post.dto.request.CommentRequestDto;
import com.example.finalproject.domain.post.entity.Comment;
import com.example.finalproject.domain.post.entity.CommentLike;
import com.example.finalproject.domain.post.entity.Post;
import com.example.finalproject.domain.post.exception.PostsNotFoundException;
import com.example.finalproject.domain.post.repository.CommentLikeRepository;
import com.example.finalproject.domain.post.repository.CommentRepository;
import com.example.finalproject.domain.post.repository.PostRepository;
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
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;

    @Transactional
    public SuccessCode createComment(Long postId, CommentRequestDto commentRequestDto, String nickname) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new PostsNotFoundException(NOT_FOUND_DATA)
        );
        List<Comment> commentList = post.getCommentList();
        User user = userRepository.findByNickname(nickname);
        Comment comment = new Comment(nickname, post, commentRequestDto.getComment(), user);
        commentList.add(comment);
        commentRepository.save(comment);

        return SuccessCode.COMMENT_CREATE_SUCCESS;
    }

    @Transactional
    public SuccessCode updateComment(Long postId, Long commentId, CommentRequestDto commentRequestDto, String nickname) {

        Comment comment = validateAuthority(commentId, nickname);
        comment.setComment(commentRequestDto.getComment());
        return SuccessCode.COMMENT_UPDATE_SUCCESS;
    }

    public SuccessCode deleteComment(Long postId, Long commentId, String nickname) {
        Comment comment = validateAuthority(commentId, nickname);
        commentRepository.delete(comment);
        return SuccessCode.COMMENT_DELETE_SUCCESS;
    }

    public SuccessCode likeComment(Long commentId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new PostsNotFoundException(NOT_FOUND_CLIENT)
        );
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new PostsNotFoundException(NOT_FOUND_DATA)
        );

        Optional<CommentLike> commentLike = commentLikeRepository.findByCommentIdAndUserUserId(commentId, userId);
        if (commentLike.isPresent()) {
            commentLikeRepository.delete(commentLike.get());
            return LIKE_CANCEL;
        } else {
            commentLikeRepository.save(new CommentLike(user, comment));
            return LIKE_SUCCESS;
        }
    }

    private Comment validateAuthority(Long id, String nickname) {
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new PostsNotFoundException(NOT_FOUND_DATA)
        );

        User user = userRepository.findByNickname(nickname);

        if (!user.getRole().equals(UserRoleEnum.ADMIN) && !comment.getUser().getUserId().equals(user.getUserId())) {
            throw new PostsNotFoundException(NO_AUTHORITY_TO_DATA);
        }

        return comment;
    }
}
