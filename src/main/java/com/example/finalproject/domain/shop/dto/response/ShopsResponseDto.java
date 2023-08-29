package com.example.finalproject.domain.shop.dto.response;

import lombok.Getter;
import org.json.JSONObject;

import java.util.List;

@Getter
public class ShopsResponseDto {
    private Integer size;
    private Integer page;
    private Integer totalCount;
    private Integer totalPages;
    private List<ShopDto> shopList;

    public ShopsResponseDto(JSONObject jsonObject, List<ShopDto> shopList) {
        this.size = jsonObject.getJSONObject("body").getInt("numOfRows");
        this.page = jsonObject.getJSONObject("body").getInt("pageNo");
        this.totalCount = jsonObject.getJSONObject("body").getInt("totalCount");
        this.totalPages = (int) Math.ceil((double) totalCount / size);
        //각 shop에 대해서 받아온 값
        this.shopList = shopList;
    }
}
