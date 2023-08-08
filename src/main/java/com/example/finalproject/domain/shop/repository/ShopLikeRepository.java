package com.example.finalproject.domain.shop.repository;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.shop.entity.ShopLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShopLikeRepository extends JpaRepository<ShopLike, Long> {
    List<ShopLike> findAllByShopId(String shopId);
    Optional<ShopLike> findByShopIdAndUser(String shopId, User user);
}
