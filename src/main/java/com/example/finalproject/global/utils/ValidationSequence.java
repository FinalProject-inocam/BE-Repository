package com.example.finalproject.global.utils;

import jakarta.validation.GroupSequence;
import com.example.finalproject.global.utils.ValidationGroups.*;
import jakarta.validation.groups.Default;

@GroupSequence({NotBlankGroup.class, PatternGroup.class, SizeGroup.class})
public interface ValidationSequence {
}
