package com.example.finalproject.domain.purchases.dto.request;

import com.example.finalproject.global.enums.UsageEnum;
import com.example.finalproject.global.validation.CustomConstraint.BirthYearLimitValidation;
import com.example.finalproject.global.validation.ValidationGroups;
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

    @BirthYearLimitValidation(groups = ValidationGroups.BirthYearLimitGroup.class)
    @NotNull(message = "출생년도는 필수입니다.", groups = ValidationGroups.NotNullGroup.class)
    private Integer birthYear;

    @NotNull(message = "이름은 필수입니다.", groups = ValidationGroups.NotNullGroup.class)
    private String name;

    @NotBlank(message = "사용목적은 필수입니다.", groups = ValidationGroups.NotBlankGroup.class)
    private String usage;

    private Boolean alarm = false;

    private String content;

    @NotBlank(message = "주소는 필수입니다.", groups = ValidationGroups.NotBlankGroup.class)
    private String addressName;

    @NotBlank(message = "우편번호는 필수입니다.", groups = ValidationGroups.NotBlankGroup.class)
    private String zoneNo;

    @NotBlank(message = "연락처는 필수입니다.", groups = ValidationGroups.NotBlankGroup.class)
    private String phoneNumber;

    private Long price;

    private String trim;
}
