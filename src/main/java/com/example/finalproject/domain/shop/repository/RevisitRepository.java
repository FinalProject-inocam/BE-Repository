package com.example.finalproject.domain.shop.repository;

import com.example.finalproject.domain.shop.entity.Revisit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevisitRepository extends JpaRepository<Revisit,Long> {
    Revisit findByReviewIdAndUserUserId(Long reviewId, Long userId);
}
