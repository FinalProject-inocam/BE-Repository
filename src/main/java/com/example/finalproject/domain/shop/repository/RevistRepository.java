package com.example.finalproject.domain.shop.repository;

import com.example.finalproject.domain.post.entity.PostLike;
import com.example.finalproject.domain.shop.entity.Revisit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RevistRepository extends JpaRepository<Revisit,Long> {
    Revisit findByReviewIdAndUserUserId(Long reviewId, Long userId);
}
