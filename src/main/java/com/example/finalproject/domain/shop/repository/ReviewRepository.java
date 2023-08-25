package com.example.finalproject.domain.shop.repository;

import com.example.finalproject.domain.shop.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByShopId(String shopId);

    List<Review> findAllByShopIdOrderByCreatedAtDesc(String shopId);

    Review findByShopId(String shopid);

    Optional<Review> findById(Long reviewId);

    List<Review> findAllByShopIdOrderByStarDesc(String shopId);

    @Query("SELECT r FROM Review r WHERE r.shopId = :shopId")
    Page<Review> findByShopIdUsingQuery(@Param("shopId") String shopId, Pageable pageable);
}
