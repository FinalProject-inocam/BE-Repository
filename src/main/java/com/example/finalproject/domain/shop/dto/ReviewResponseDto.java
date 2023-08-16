package com.example.finalproject.domain.shop.dto;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.post.entity.Image;
import com.example.finalproject.domain.shop.entity.Review;
import com.example.finalproject.domain.shop.entity.ReviewImage;
import com.example.finalproject.domain.shop.entity.ReviewLike;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ReviewResponseDto {
    private Long reviewId;
    private String review;
    private String nickname;
    private Integer star;
    private Boolean revisit;
    private List<String> imageUrls;
    private LocalDateTime createAt;
    private LocalDateTime modifiedAt;
    private Boolean isLike;
    public ReviewResponseDto(Review review, User user) {
        this.reviewId = review.getId();
        this.nickname = review.getUser().getNickname();
        this.review = review.getReview();
        this.star = review.getStar();
        this.revisit=review.getRevisit();
        this.imageUrls = review.getImageUrls()
                .stream()
                .map(ReviewImage::getImage)
                .toList();
        this.createAt = review.getCreatedAt();
        this.modifiedAt = review.getModifiedAt();
        this.isLike = user != null && review.getReviewLikes().stream()
                .map(ReviewLike::getUser)
                .map(User::getUserId)
                .anyMatch(userId -> userId.equals(user.getUserId()));
    }
}
