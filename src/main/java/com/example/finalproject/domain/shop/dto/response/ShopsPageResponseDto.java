package com.example.finalproject.domain.shop.dto.response;

import com.example.finalproject.domain.shop.dto.ShopPageDto;
import com.example.finalproject.domain.shop.entity.Shop;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class ShopsPageResponseDto {
    private Integer size;
    private Integer totalCount;
    private Integer totalPages;
    private Long totalElements;
    private Boolean last;
    private Boolean first;
    private int currentPage;
    private List<ShopPageDto> shopList;

    public ShopsPageResponseDto(Page<Shop> shopPage, List<ShopPageDto> shopList) {
        this.currentPage=shopPage.getNumber()+1;
        this.totalElements=shopPage.getTotalElements();
        this.last=shopPage.isLast();
        this.first=shopPage.isFirst();
        this.size = shopPage.getSize();
        this.totalCount = (int) shopPage.getTotalElements();
        this.totalPages = (int) Math.ceil((double) totalCount / size);
        //각 shop에 대해서 받아온 값
        this.shopList = shopList;
    }
}
