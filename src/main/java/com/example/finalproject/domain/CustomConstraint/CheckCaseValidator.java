package com.example.finalproject.domain.CustomConstraint;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Year;

public class CheckCaseValidator implements ConstraintValidator<CheckCase, Integer> {

    private Integer validatedYear;

    @Override
    public void initialize(CheckCase constraintAnnotation) {
        Integer currentYear = Year.now().getValue();
        Integer validatedYear = currentYear - 18;//만 18세 이상이어야 차량면허 소지가 가능
        this.validatedYear = validatedYear;
    }

    @Override
    public boolean isValid(Integer object, ConstraintValidatorContext constraintContext) {
        if (object == null) {
            return true;
        }

        if (object > validatedYear) {
            return false;
        }
        return true;
    }
}