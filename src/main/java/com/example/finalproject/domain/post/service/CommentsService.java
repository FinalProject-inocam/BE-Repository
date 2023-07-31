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

import java.util.List;

import static com.example.finalproject.global.enums.ErrorCode.NOT_FOUND_DATA;

@Service
@RequiredArgsConstructor
public class CommentsService {
    private final PostCommentsRepository postCommentsRepository;
    private final PostsRepository postsRepository;
    public SuccessCode createService(Long postid,CommentRequestDto commentRequestDto,String nickname) {
        Posts post =postsRepository.findById(postid).orElseThrow(
                () -> new PostsNotFoundException(NOT_FOUND_DATA)
        );
        List<Comments> commentsList=post.getCommentList();

        return SuccessCode.COMMENT_CREATE_SUCCESS;
    }
}
