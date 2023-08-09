package com.example.finalproject.domain.shop.dto;

import com.example.finalproject.domain.post.entity.Image;
import com.example.finalproject.domain.shop.entity.Review;
import com.example.finalproject.domain.shop.entity.ReviewImage;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ReviewResponseDto {
    private Long reviewId;
    private String review;
    private String nickname;
    private Integer star;
    private List<ReviewImage> imageUrls;
    private LocalDateTime createAt;
    private LocalDateTime modifiedAt;

    public ReviewResponseDto(Review review) {
        this.reviewId = review.getId();
        this.nickname = review.getUser().getNickname();
        this.review = review.getReview();
        this.star = review.getStar();
        this.imageUrls = review.getImageUrls();
        this.createAt = review.getCreatedAt();
        this.modifiedAt = review.getModifiedAt();
    }
}
