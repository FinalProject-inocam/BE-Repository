package com.example.finalproject.domain.shop.dto;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.shop.entity.Review;
import com.example.finalproject.domain.shop.entity.ShopLike;
import com.example.finalproject.domain.shop.repository.ReviewLikeRepository;
import lombok.Getter;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ShopOneResponseDto {
    private String shopId;
    private String shopName;
    private String address;
    private Double longitude;
    private Double latitude;
    private Boolean isLike;
    private Integer likeCount;
    private Double avgStar;
    private List<ReviewResponseDto> reviews = new ArrayList<>();
    private List<String> banner;

    public ShopOneResponseDto(JSONObject itemJson, List<Review> reviews, List<ShopLike> shopLikes, User user, List<String>banner, ReviewLikeRepository reviewLikeRepository) {
        this.shopId = itemJson.getString("bizesId");
        this.shopName = itemJson.getString("bizesNm");
        this.address = itemJson.getString("rdnmAdr");
        if (this.address.isEmpty()) {
            this.address = itemJson.getString("lnoAdr");
        }
        this.longitude = itemJson.getDouble("lon");
        this.latitude = itemJson.getDouble("lat");
        //shoplike 전체중 user가 쓴글 찾기 기본값 false
        this.isLike = user != null && shopLikes.stream()
                .map(ShopLike::getUser)
                .map(User::getUserId)
                .anyMatch(userId -> userId.equals(user.getUserId()));
        //shoplike전체 숫자 세기
        this.likeCount = shopLikes.size();
        //comment전체 가져와서 그 중 star값 평균
        this.avgStar = reviews.stream()
                .mapToInt(Review::getStar)
                .average()
                .orElse(0);
        //comment전체값 출력
        for(Review review : reviews){
            ReviewResponseDto reviewResponseDto=new ReviewResponseDto(review,user);
            this.reviews.add(reviewResponseDto);
        }
        this.banner=banner;
    }
}
