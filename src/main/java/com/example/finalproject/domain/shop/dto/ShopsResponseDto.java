package com.example.finalproject.domain.shop.dto;

import com.example.finalproject.domain.auth.entity.User;
import com.example.finalproject.domain.shop.entity.Review;
import com.example.finalproject.domain.shop.entity.ShopLike;
import lombok.Getter;
import org.json.JSONObject;

import java.util.List;

@Getter
public class ShopsResponseDto {
    private Integer numOfRows;
    private Integer pageNo;
    private Integer totalCount;
    private List<ShopDto> shopDtoList;
//    private List<ReviewResponseDto> commentsList;

    public ShopsResponseDto(JSONObject jsonObject, List<ShopDto> shopDtoList) {
        this.numOfRows = jsonObject.getJSONObject("body").getInt("numOfRows");
        this.pageNo = jsonObject.getJSONObject("body").getInt("pageNo");
        this.totalCount = jsonObject.getJSONObject("body").getInt("totalCount");
        //각 shop에 대해서 받아온 값
        this.shopDtoList = shopDtoList;
    }
}
