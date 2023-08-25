package com.example.finalproject.domain.shop.entity;

import com.example.finalproject.domain.auth.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class ReviewLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String shopId;

    @Column(nullable = false)
    private Long reviewId;

    @ManyToOne
    @JoinColumn(name = "user_Id", nullable = false)
    private User user;

    public ReviewLike(User user, Long reviewId, String shopId) {
        this.reviewId = reviewId;
        this.user = user;
        this.shopId = shopId;
    }
}
