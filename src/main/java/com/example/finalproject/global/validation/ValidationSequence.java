package com.example.finalproject.global.validation;

import com.example.finalproject.global.validation.ValidationGroups.*;
import jakarta.validation.GroupSequence;

@GroupSequence({NotNullGroup.class, NotBlankGroup.class,
        PatternGroup.class, PastGroup.class,
        SizeGroup.class, MinGroup.class,
        MaxGroup.class, BirthYearLimitGroup.class})
public interface ValidationSequence {
}
