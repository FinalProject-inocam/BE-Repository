package com.example.finalproject.domain.shop.entity;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.shop.dto.ReviewRequestDto;
import com.example.finalproject.global.utils.Timestamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@BatchSize(size = 30)
public class Review extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String review;

    @Column(nullable = false)
    private Integer star;

    @Column(nullable = false)
    private Boolean revisit;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "review_id")
    private List<ReviewImage> imageUrls = new ArrayList<>();

    @Column(nullable = false)
    private String shopId;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn
    private List<ReviewLike> reviewLikes = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    public Review(ReviewRequestDto requestDto, String shopId, User user) {
        this.review = requestDto.getReview();
        this.star = requestDto.getStar();
        this.user = user;
        this.shopId = shopId;
        this.revisit = requestDto.getRevisit();
    }

    public void update(ReviewRequestDto requestDto) {
        this.review = requestDto.getReview();
        this.star = requestDto.getStar();
    }

    public void updateRevisit(Boolean revisit) {
        this.revisit = revisit;
    }
}
