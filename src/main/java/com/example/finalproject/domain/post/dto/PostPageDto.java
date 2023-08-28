package com.example.finalproject.domain.post.dto;

import com.example.finalproject.domain.shop.dto.ReviewResponseDto;
import com.example.finalproject.global.responsedto.PageResponse;
import lombok.Getter;
import org.springframework.data.domain.Sort;

import java.util.List;

@Getter
public class PostPageDto {
    private List<PostAllResponseDto> content;
    private int size;
    private Long totalElements;
    private int totalPages;
    private Boolean last;
    private Boolean first;
    private int currentPage;

    public PostPageDto(PageResponse pageResponse) {
        this.content=pageResponse.getContent();
        this.size=pageResponse.getSize();
        this.totalElements=pageResponse.getTotalElements();
        this.last=pageResponse.isLast();
        this.first=pageResponse.isFirst();
        this.currentPage=pageResponse.getNumber();
    }
}
