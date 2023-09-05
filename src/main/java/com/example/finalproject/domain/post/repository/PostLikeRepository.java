package com.example.finalproject.domain.post.repository;

import com.example.finalproject.domain.post.entity.PostLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByPostIdAndUserUserId(Long postId, Long userId);

    Long countByPostId(Long id);

    Boolean existsByPostIdAndUserUserId(Long id, Long userId);

    Optional<Object> deleteByPostIdAndUserUserId(Long postId, Long userId);

    Page<PostLike> findByUserUserId(Long userId, Pageable pageable);
}
