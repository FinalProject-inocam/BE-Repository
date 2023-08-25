package com.example.finalproject.domain.purchases.dto;

import com.example.finalproject.domain.CustomConstraint.CheckCase;
import com.example.finalproject.global.utils.ValidationGroups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PurchasesRequestDto {
    @NotBlank(message = "차량 타입은 필수입니다.", groups = ValidationGroups.NotBlankGroup.class)
    private String type;

    @NotBlank(message = "색상은 필수입니다.", groups = ValidationGroups.NotBlankGroup.class)
    private String color;

    @NotBlank(message = "성별은 필수입니다.", groups = ValidationGroups.NotBlankGroup.class)
    private String gender;

    @CheckCase(message = "18세 미만은 차량신청이 불가능합니다.", groups = ValidationGroups.ChaeckCaseGroup.class)
    @NotNull(message = "출생년도는 필수입니다.", groups = ValidationGroups.NotNullGroup.class)
    private Integer birthYear;

    private Boolean alarm = false;

    private String content;

    @NotBlank(message = "주소는 필수입니다.", groups = ValidationGroups.NotBlankGroup.class)
    private String addressName;

    @NotBlank(message = "우편번호는 필수입니다.", groups = ValidationGroups.NotBlankGroup.class)
    private String zoneNo;
}
