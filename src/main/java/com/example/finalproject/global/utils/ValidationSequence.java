package com.example.finalproject.global.utils;

import com.example.finalproject.global.utils.ValidationGroups.*;
import jakarta.validation.GroupSequence;

@GroupSequence({NotNullGroup.class, NotBlankGroup.class, PatternGroup.class, SizeGroup.class, MinGroup.class, MaxGroup.class})
public interface ValidationSequence {
}
