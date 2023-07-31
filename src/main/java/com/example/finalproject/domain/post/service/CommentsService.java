package com.example.finalproject.domain.post.service;

import com.example.finalproject.domain.post.dto.CommentRequestDto;
import com.example.finalproject.domain.post.entity.Comments;
import com.example.finalproject.domain.post.entity.Posts;
import com.example.finalproject.domain.post.exception.PostsNotFoundException;
import com.example.finalproject.domain.post.repository.PostCommentsRepository;
import com.example.finalproject.domain.post.repository.PostsRepository;
import com.example.finalproject.global.enums.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.finalproject.global.enums.ErrorCode.NOT_FOUND_DATA;

@Service
@RequiredArgsConstructor
public class CommentsService {
    private final PostCommentsRepository postCommentsRepository;
    private final PostsRepository postsRepository;
    @Transactional
    public SuccessCode createService(Long postId,CommentRequestDto commentRequestDto,String nickName) {
        Posts post =postsRepository.findById(postId).orElseThrow(
                () -> new PostsNotFoundException(NOT_FOUND_DATA)
        );
        List<Comments> commentsList=post.getCommentList();
//        User user = userRepository.findByNickname(nickname).orElseThrow(
//                () -> new UserException(USER_NOT_FOUND)
//        );
        Comments comment = new Comments(nickName,post,commentRequestDto.getComment());
        commentsList.add(comment);
        postCommentsRepository.save(comment);

        return SuccessCode.COMMENT_CREATE_SUCCESS;
    }
    @Transactional
    public SuccessCode updateComment(Long postId, Long commentId, CommentRequestDto commentRequestDto){
        Comments comments =postCommentsRepository.findById(commentId).orElseThrow(
                () -> new PostsNotFoundException(NOT_FOUND_DATA)// 나중에 예외 추가해서 고치기
        );
        comments.setComment(commentRequestDto.getComment());
        return SuccessCode.COMMENT_UPDATE_SUCCESS;
    }


    public SuccessCode deleteComments(Long postId, Long commentId) {
        Comments comments =postCommentsRepository.findById(commentId).orElseThrow(
                () -> new PostsNotFoundException(NOT_FOUND_DATA)// 나중에 예외 추가해서 고치기
        );
        postCommentsRepository.delete(comments);
        return SuccessCode.COMMENT_DELETE_SUCCESS;
    }
}
