package ru.scriptrid.reviewservice.model.dto;

import org.hibernate.validator.constraints.Range;
import org.springframework.validation.annotation.Validated;

@Validated
public record ReviewCreateDto(
        long orderId,
        @Range(min = 1, max = 5)
        int rating,

        String text
) {
}
