package ru.scriptrid.reviewservice.controller;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.scriptrid.common.security.JwtAuthenticationToken;
import ru.scriptrid.reviewservice.model.dto.ReviewCreateDto;
import ru.scriptrid.reviewservice.model.dto.ReviewDto;
import ru.scriptrid.reviewservice.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/api/review/")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ReviewDto publishReview(@AuthenticationPrincipal JwtAuthenticationToken token,
                                   @RequestBody @Valid ReviewCreateDto dto) {
        return reviewService.addReview(token, dto);
    }

    @GetMapping("/byProduct")
    public List<ReviewDto> getReviewsByProduct(@RequestParam long productId) {
        return reviewService.getReviewsByProductId(productId);
    }

    @GetMapping("/byOrder")
    public ReviewDto getReviewByOrder(@RequestParam long orderId) {
        return reviewService.getReviewByOrderId(orderId);
    }

    @GetMapping("/{id}")
    public ReviewDto getReviewById(@PathVariable long id) {
        return reviewService.getReviewById(id);
    }

    @GetMapping("/byUser")
    public List<ReviewDto> getReviewsByUser(@RequestParam long userId) {
        return reviewService.getReviewByUserId(userId);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public List<ReviewDto> getAllReviews() {
        return reviewService.getAllReviews();
    }
}
