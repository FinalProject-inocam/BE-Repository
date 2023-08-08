package com.example.finalproject.domain.shop.entity;

import com.example.finalproject.domain.auth.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class ShopLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String shopId;

    @ManyToOne
    @JoinColumn(name = "user_Id", nullable = false)
    private User user;

    public ShopLike(String shopId, User user) {
        this.shopId = shopId;
        this.user = user;
    }
}
