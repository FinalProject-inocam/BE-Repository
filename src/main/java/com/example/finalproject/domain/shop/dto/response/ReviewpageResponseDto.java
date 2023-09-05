package com.example.finalproject.domain.shop.dto.response;

import com.example.finalproject.global.responsedto.PageResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class ReviewpageResponseDto {
    private List<ReviewResponseDto> content;
    private int size;
    private Long totalElements;
    private int totalPages;
    private Boolean last;
    private Boolean first;
    private int currentPage;
    private List<String> bannerList;
    private Double avgStar;
    private Long reviewCount;
//배너 ,별점, 리뷰 갯수
    public ReviewpageResponseDto(PageResponse pageResponse, List<String> bannerList, Double avgStar, int reviewCount,int page,int size) {
        this.content = pageResponse.getContent();
        this.size = pageResponse.getSize();
        this.totalPages = (int) Math.ceil((double) pageResponse.getTotalElements() / size);
        this.totalElements = pageResponse.getTotalElements();
        if(totalPages==page)
        {
            this.last = true;
        }
        else{
            this.last=false;
        }

        if(page==1) {
            this.first = true;
        }
        else{
            this.first=false;
        }
        this.currentPage = page;
        this.bannerList = bannerList;
        this.avgStar = avgStar;
        this.reviewCount=Long.valueOf(reviewCount);
    }
}