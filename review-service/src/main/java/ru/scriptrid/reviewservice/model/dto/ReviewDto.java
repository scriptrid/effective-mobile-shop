package ru.scriptrid.reviewservice.model.dto;

import java.time.ZonedDateTime;

public record ReviewDto(
        long reviewId,
        long productId,
        long authorId,
        int rating,
        String text,
        ZonedDateTime timeOfReview
) {

}
