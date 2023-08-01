package com.example.finalproject.domain.post.repository;

import com.example.finalproject.domain.post.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike,Long> {
    Optional<PostLike> findByPostsIdAndUserUserId(Long postId, Long userId);

    Long countByPostsId(Long id);

    Boolean existsByUserUserId(Long userId);
}
