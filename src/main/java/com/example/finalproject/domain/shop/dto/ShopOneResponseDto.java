package com.example.finalproject.domain.shop.dto;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.shop.entity.Review;
import com.example.finalproject.domain.shop.entity.Shop;
import com.example.finalproject.domain.shop.entity.ShopLike;
import lombok.Getter;

import java.util.Arrays;
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
    private Long reviewCount;
    private List<String> banner;
    private Integer reviewImageSize;
    private String bussinessHour = "08:00~16:00";
    private String phoneNumber = "02-1234-1234";
    private String bussinessDay = "연중무휴";
    private String detail;
    private List<String> tag = Arrays.asList("자동차랩핑", "디테일링 세차", "디테일링 광택", "자동차 튜닝", "자동차 썬팅");

    public ShopOneResponseDto(Shop shop, List<Review> reviews,
                              List<ShopLike> shopLikes, User user,
                              List<String> banner, Integer reviewImageSize) {
        this.shopId = shop.getShopId();
        this.shopName = shop.getShopName();
        this.address = shop.getAddress();
//        if (this.address.isEmpty()) {
//            this.address = itemJson.getString("lnoAdr");
//        }
        reviewCount = Long.valueOf(reviews.size());
        this.longitude = shop.getLongitude();
        this.latitude = shop.getLatitude();
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

        this.banner = banner;
        this.reviewImageSize = reviewImageSize;
        this.detail = "안녕하세요, 랩핑/PPF 전문 " + shop.getShopName() + "입니다. 랩핑 PPF는 철저한 사후관리가 필수입니다. 맡겨 주시는 만큼 최선을 다해 시공하고, 좋은 품질의 자재와 정확한 기술로 오랜 시간동안 깨끗하고 선명한 상태를 유지하도록 도와드립니다.";
    }
}
