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
        this.currentPage = page;
        this.totalPages = (int)pageResponse.getTotalElements()/size;
        this.size = pageResponse.getSize();
        this.totalElements = pageResponse.getTotalElements();
        if(totalPages == page)
        {
            this.last = true;
        }
        else {
            this.last = false;
        }

        if(page == 1) {
            this.first = true;
        }
        else {
            this.first = false;
        }
    }
}
