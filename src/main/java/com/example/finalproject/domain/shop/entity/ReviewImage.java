package com.example.finalproject.domain.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ReviewImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewImageId;

    @Column(nullable = false)
    private String image;

    public ReviewImage(String imageUrl) {
        this.image = imageUrl;
    }
}