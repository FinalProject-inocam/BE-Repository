package com.example.finalproject.domain.shop.dto;

import com.example.finalproject.global.responsedto.PageResponse;
import lombok.Getter;
import org.springframework.data.domain.Sort;

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
    private Sort sort;

    public ReviewpageResponseDto(PageResponse pageResponse) {
        this.content = pageResponse.getContent();
        this.size = pageResponse.getSize();
        this.totalPages = pageResponse.getTotalPages();
        this.totalElements = pageResponse.getTotalElements();
        this.last = pageResponse.isLast();
        this.first = pageResponse.isFirst();
        this.sort = pageResponse.getSort();
        this.currentPage = pageResponse.getNumber();
    }
}