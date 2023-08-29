package com.example.finalproject.domain.post.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class PostSelectDelDto {
    List<Long> postIdList;
}
