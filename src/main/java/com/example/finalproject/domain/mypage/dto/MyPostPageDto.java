package com.example.finalproject.domain.mypage.dto;

import com.example.finalproject.domain.post.dto.response.PostAllResponseDto;
import com.example.finalproject.global.responsedto.PageResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class MyPostPageDto {
    private List<PostListDto> content;
    private int size;
    private Long totalElements;
    private int totalPages;
    private Boolean last;
    private Boolean first;
    private int currentPage;

    public MyPostPageDto(PageResponse pageResponse) {
        this.content = pageResponse.getContent();
        this.size = pageResponse.getSize();
        this.totalElements = pageResponse.getTotalElements();
        this.totalPages = pageResponse.getTotalPages();
        this.last = pageResponse.isLast();
        this.first = pageResponse.isFirst();
        this.currentPage = pageResponse.getNumber() + 1;
    }
}
