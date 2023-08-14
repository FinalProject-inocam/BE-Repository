package com.example.finalproject.global.responsedto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true, value = {"pageable"})
public class PageResponse<T> extends PageImpl<T> {

    public PageResponse(@JsonProperty("content") List<T> content, @JsonProperty("page") int page,
                        @JsonProperty("size") int size, @JsonProperty("totalElements") long totalElements) {
        super(content, PageRequest.of(page, size), totalElements);
    }

    public PageResponse(Page<T> page) {
        super(page.getContent(), page.getPageable(), page.getTotalElements());
    }

    public PageResponse(List<T> content, Pageable pageable, long totalElements) {
        super(content, pageable, totalElements);
    }
}






