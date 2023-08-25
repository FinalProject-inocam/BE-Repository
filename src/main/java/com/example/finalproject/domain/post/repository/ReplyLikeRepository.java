package com.example.finalproject.domain.post.repository;

import com.example.finalproject.domain.post.entity.ReplyLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReplyLikeRepository extends JpaRepository<ReplyLike,Long> {
    Optional<ReplyLike> findByReplyIdAndUserUserId(Long replyId, Long userId);

    Long countByReplyId(Long id);

    Boolean existsByReplyIdAndUserUserId(Long id, Long userId);
}
