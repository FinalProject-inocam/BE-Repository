package com.example.finalproject.domain.post.repository;

import com.example.finalproject.domain.post.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByCommentIdAndUserUserId(Long commentId, Long userId);

    Long countByCommentId(Long id);

    Boolean existsByCommentIdAndUserUserId(Long id, Long userId);
}
