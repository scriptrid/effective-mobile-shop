package ru.scriptrid.productservice.model.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Set;

@Validated
public record DiscountCreateDto(

        @NotEmpty
        Set<Long> productIds,

        @Range(min = 0, max = 1)
        BigDecimal priceModifier,

        @Nullable
        ZonedDateTime discountStart,

        @Nullable
        ZonedDateTime discountEnd
)  {
}
