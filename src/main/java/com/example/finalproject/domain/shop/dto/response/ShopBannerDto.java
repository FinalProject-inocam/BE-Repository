package com.example.finalproject.domain.shop.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ShopBannerDto {
    private Long likeCount;
    private List<String> imgUrls;

    public ShopBannerDto(Long count, List<String> imgUrls) {
        this.likeCount = count;
        this.imgUrls = imgUrls;
    }
}
