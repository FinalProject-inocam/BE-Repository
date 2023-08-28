package com.example.finalproject.domain.shop.dto;

import com.example.finalproject.domain.shop.entity.Shop;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class ShopsPageResponseDto {
    private Integer size;
    private Integer page;
    private Integer totalCount;
    private Integer totalPages;
    private List<ShopPageDto> shopList;

    public ShopsPageResponseDto(Page<Shop> shopPage, List<ShopPageDto> shopList) {
        this.size = shopPage.getSize();
        this.page = shopPage.getNumber() + 1;
        this.totalCount = (int) shopPage.getTotalElements();
        this.totalPages = (int) Math.ceil((double) totalCount / size);
        //각 shop에 대해서 받아온 값
        this.shopList = shopList;
    }
}
