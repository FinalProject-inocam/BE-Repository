package com.example.finalproject.domain.shop.repository;

import com.example.finalproject.domain.shop.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByShopId(String shopId);
    Optional<Review> findById(Long reviewId);
}