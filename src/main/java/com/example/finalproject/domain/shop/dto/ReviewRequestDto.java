package com.example.finalproject.domain.shop.dto;

import com.example.finalproject.global.utils.ValidationGroups.MaxGroup;
import com.example.finalproject.global.utils.ValidationGroups.MinGroup;
import com.example.finalproject.global.utils.ValidationGroups.NotBlankGroup;
import com.example.finalproject.global.utils.ValidationGroups.NotNullGroup;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ReviewRequestDto {
    @NotBlank(message = "후기는 필수입니다.", groups = NotBlankGroup.class)
    private String review;

    @NotNull(message = "별점은 필수입니다.", groups = NotNullGroup.class)
    @Min(value = 0, message = "0점 미만의 별점은 줄 수 없습니다.", groups = MinGroup.class)
    @Max(value = 5, message = "5점 초과의 별점은 줄 수 없습니다.", groups = MaxGroup.class)
    private Integer star;

    private Boolean revisit;
}
