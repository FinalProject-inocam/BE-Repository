package com.example.finalproject.domain.post.dto;

import com.example.finalproject.domain.post.dto.response.CommentResponseDto;
import com.example.finalproject.global.responsedto.PageResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class CommentPageDto {
    private int size;
    private Long totalElements;
    private int totalPages;
    private Boolean last;
    private Boolean first;
    private int currentPage;
    private List<CommentResponseDto> content;

    public CommentPageDto(PageResponse pageResponse) {
        this.content = pageResponse.getContent();
        this.currentPage = pageResponse.getNumber() + 1;
        this.totalPages = pageResponse.getTotalPages();
        this.size = pageResponse.getSize();
        this.totalElements = pageResponse.getTotalElements();
        this.last = pageResponse.isLast();
        this.first = pageResponse.isFirst();
    }
}
