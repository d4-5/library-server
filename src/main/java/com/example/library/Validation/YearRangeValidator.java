package com.example.library.Validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class YearRangeValidator implements ConstraintValidator<YearRange, Integer> {

    @Override
    public void initialize(YearRange constraintAnnotation) {
    }

    @Override
    public boolean isValid(Integer year, ConstraintValidatorContext context) {
        if (year == null) {
            return false;
        }
        int currentYear = LocalDate.now().getYear();
        return year > 0 && year <= currentYear;
    }
}