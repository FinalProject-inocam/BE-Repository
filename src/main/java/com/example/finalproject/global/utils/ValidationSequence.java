package com.example.finalproject.global.utils;

import com.example.finalproject.global.utils.ValidationGroups.*;
import jakarta.validation.GroupSequence;

@GroupSequence({NotNullGroup.class, NotBlankGroup.class,
        PatternGroup.class, PastGroup.class,
        SizeGroup.class, MinGroup.class,
        MaxGroup.class, ChaeckCaseGroup.class})
public interface ValidationSequence {
}
