package com.example.finalproject.domain.post.service;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.auth.repository.UserRepository;
import com.example.finalproject.domain.post.dto.CommentRequestDto;
import com.example.finalproject.domain.post.entity.Comment;
import com.example.finalproject.domain.post.entity.CommentLike;
import com.example.finalproject.domain.post.entity.Reply;
import com.example.finalproject.domain.post.entity.ReplyLike;
import com.example.finalproject.domain.post.exception.PostsNotFoundException;
import com.example.finalproject.domain.post.repository.*;
import com.example.finalproject.global.enums.SuccessCode;
import com.example.finalproject.global.enums.UserRoleEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.example.finalproject.global.enums.ErrorCode.*;
import static com.example.finalproject.global.enums.SuccessCode.LIKE_CANCEL;
import static com.example.finalproject.global.enums.SuccessCode.LIKE_SUCCESS;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "postService")
public class ReplyService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ReplyRepository replyRepository;
    private final ReplyLikeRepository replyLikeRepository;

    @Transactional
    public SuccessCode createReply(Long commentId, CommentRequestDto commentRequestDto, User user) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new PostsNotFoundException(NOT_FOUND_DATA)
        );
        List<Reply> replyList = comment.getReplyList();
        Reply reply = new Reply(comment, commentRequestDto.getComment(), user);
        replyRepository.save(reply);
        replyList.add(reply);
        return SuccessCode.COMMENT_CREATE_SUCCESS;
    }

    @Transactional
    public SuccessCode updateReply(Long replyId, CommentRequestDto commentRequestDto, User user) {
        Reply reply = validateAuthority(replyId, user.getNickname());
        reply.updateComment(commentRequestDto.getComment());
        return SuccessCode.COMMENT_UPDATE_SUCCESS;
    }
    @Transactional
    public SuccessCode deleteReply(Long replyId, User user) {
        Reply reply = validateAuthority(replyId, user.getNickname());
        replyLikeRepository.deleteByUserUserIdAndReplyId(user.getUserId(),replyId);
        replyRepository.delete(reply);
        return SuccessCode.COMMENT_DELETE_SUCCESS;
    }

    public SuccessCode likeReply(Long replyId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new PostsNotFoundException(NOT_FOUND_CLIENT)
        );
        Reply reply = replyRepository.findById(replyId).orElseThrow(
                () -> new PostsNotFoundException(NOT_FOUND_DATA)
        );

        Optional<ReplyLike> replyLike = replyLikeRepository.findByReplyIdAndUserUserId(replyId, userId);
        if (replyLike.isPresent()) {
            replyLikeRepository.delete(replyLike.get());
            return LIKE_CANCEL;
        } else {
            replyLikeRepository.save(new ReplyLike(user, reply));
            return LIKE_SUCCESS;
        }
    }

    private Reply validateAuthority(Long id, String nickname) {
        Reply reply = replyRepository.findById(id).orElseThrow(
                () -> new PostsNotFoundException(NOT_FOUND_DATA)
        );

        User user = userRepository.findByNickname(nickname);

        if (!user.getRole().equals(UserRoleEnum.ADMIN) && !reply.getUser().getUserId().equals(user.getUserId())) {
            throw new PostsNotFoundException(NO_AUTHORITY_TO_DATA);
        }
        return reply;
    }
}
