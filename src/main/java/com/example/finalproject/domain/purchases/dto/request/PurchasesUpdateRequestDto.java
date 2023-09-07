package com.example.finalproject.domain.purchases.dto.request;

import com.example.finalproject.global.validation.CustomConstraint.BirthYearLimitValidation;
import com.example.finalproject.global.validation.ValidationGroups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PurchasesUpdateRequestDto {
    @NotBlank(message = "색상은 필수입니다.", groups = ValidationGroups.NotBlankGroup.class)
    private String color;

    @NotBlank(message = "사용목적은 필수입니다.", groups = ValidationGroups.NotBlankGroup.class)
    private String usage;

    private String content;

    @NotNull(message = "이름은 필수입니다.", groups = ValidationGroups.NotNullGroup.class)
    private String name;

    @NotBlank(message = "연락처는 필수입니다.", groups = ValidationGroups.NotBlankGroup.class)
    private String phoneNumber;

    @NotBlank(message = "주소는 필수입니다.", groups = ValidationGroups.NotBlankGroup.class)
    private String addressName;

    @NotBlank(message = "우편번호는 필수입니다.", groups = ValidationGroups.NotBlankGroup.class)
    private String zoneNo;

    private Boolean alarm = false;

}
