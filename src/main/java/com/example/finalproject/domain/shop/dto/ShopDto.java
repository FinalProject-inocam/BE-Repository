package com.example.finalproject.domain.shop.dto;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.shop.entity.Review;
import com.example.finalproject.domain.shop.entity.ShopLike;
import lombok.Getter;
import org.json.JSONObject;

import java.util.List;

@Getter
public class ShopDto {
    private String shopId;
    private String shopName;
    private String lnoAdr; //지번 주소
    private String rdnmAdr; //도로명 주소
    private Double longitude;
    private Double latitude;
    private Boolean isLike;
    private Integer likeCount;
    private Double avgStar;

    public ShopDto(JSONObject itemJson, List<Review> reviews, List<ShopLike> shopLikes, User user) {
        this.shopId = itemJson.getString("bizesId");
        this.shopName = itemJson.getString("bizesNm");
        this.lnoAdr = itemJson.getString("lnoAdr");
        this.rdnmAdr = itemJson.getString("rdnmAdr");
        this.longitude = itemJson.getDouble("lon");
        this.latitude = itemJson.getDouble("lat");
        //shoplike 전체중 user가 쓴글 찾기
        this.isLike = user == null ? false : shopLikes.stream()
                .map(ShopLike::getUser)
                .map(User::getUserId)
                .toList()
                .contains(user.getUserId());
        //shoplike전체 숫자 세기
        this.likeCount = shopLikes.size();
        //comment전체 가져와서 그 중 star값 평균
        this.avgStar = reviews.stream()
                .map(Review::getStar)
                .mapToInt(Integer::intValue)
                .average().orElse(0);
    }
}
