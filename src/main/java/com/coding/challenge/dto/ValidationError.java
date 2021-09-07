package com.coding.challenge.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public class ValidationError {
    private final String fieldName;
    private final String message;
}
