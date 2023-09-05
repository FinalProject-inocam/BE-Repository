package com.example.finalproject.global.validation.CustomConstraint;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ProhibitValidator implements ConstraintValidator<ProhibitValidation, String> {

    private String admin;
    private String server;
    private String date;

    private String prohibit;

    @Override
    public void initialize(ProhibitValidation constraintAnnotation) {
        this.admin  = "E001";
        this.server = "server";
        this.date = "date";
        this.prohibit = "!";
    }

    @Override
    public boolean isValid(String object, ConstraintValidatorContext constraintContext) {
        if (object == null) {
            return true;
        }

        if (object.equals(admin) || object.equals(server) || object.equals(date) || object.contains(prohibit)) {
            return false;
        }

        return true;
    }
}