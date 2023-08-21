package com.example.finalproject.domain.shop.repository;

import com.example.finalproject.domain.shop.entity.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike,Long> {

    Optional<ReviewLike> findByReviewIdAndUserUserId(Long reviewId, Long userId);

    Boolean existsByReviewIdAndUserUserId(Long id, Long userId);

    Long countByReviewId(Long id);
}
