package ru.scriptrid.reviewservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.scriptrid.common.dto.OrderDto;
import ru.scriptrid.common.exception.InvalidOwnerException;
import ru.scriptrid.common.security.JwtAuthenticationToken;
import ru.scriptrid.reviewservice.exceptions.OrderForReviewNotFoundException;
import ru.scriptrid.reviewservice.exceptions.ReviewAlreadyExists;
import ru.scriptrid.reviewservice.exceptions.ReviewNotFoundByIdException;
import ru.scriptrid.reviewservice.exceptions.ReviewNotFoundByOrderIdException;
import ru.scriptrid.reviewservice.model.dto.ReviewCreateDto;
import ru.scriptrid.reviewservice.model.dto.ReviewDto;
import ru.scriptrid.reviewservice.model.entity.ReviewEntity;
import ru.scriptrid.reviewservice.repository.ReviewRepository;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@Slf4j
public class ReviewService {
    private final WebOrderService webOrderService;
    private final ReviewRepository reviewRepository;

    public ReviewService(WebOrderService webOrderService, ReviewRepository reviewRepository) {
        this.webOrderService = webOrderService;
        this.reviewRepository = reviewRepository;
    }

    @Transactional
    public ReviewDto addReview(JwtAuthenticationToken token, ReviewCreateDto dto) {
        OrderDto order = webOrderService.getDto(dto.orderId());
        if (order == null) {
            log.warn("Order by id \"{}\" for review not found", dto.orderId());
            throw new OrderForReviewNotFoundException(dto.orderId());
        }
        if (token.getId() != order.customerId()) {
            log.warn("User with id \"{}\" is not an owner of order with id \"{}\"", token.getId(), order.id());
            throw new InvalidOwnerException(order.id(), order.customerId(), token.getId());
        }
        if (reviewRepository.existsByAuthorIdAndOrderId(token.getId(), dto.orderId())) {
            log.warn("User with id \"{}\" already has a review on product by id \"{}\"", token.getId(), order.productId());
            throw new ReviewAlreadyExists(token.getId(), order.productId());
        }
        ReviewEntity review = reviewRepository.save(toEntity(order, dto));
        return toDto(review);
    }

    public ReviewDto getReviewById(long id) {
        return reviewRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> {
                    log.warn("Review by id \"{}\" not found", id);
                    return new ReviewNotFoundByIdException(id);
                });
    }

    public ReviewDto getReviewByOrderId(long orderId) {
        return reviewRepository.findByOrderId(orderId)
                .map(this::toDto)
                .orElseThrow(() -> {
                    log.warn("Review by order id \"{}\" not found", orderId);
                    return new ReviewNotFoundByOrderIdException(orderId);
                });
    }

    public List<ReviewDto> getReviewsByProductId(long productId) {
        return reviewRepository.getReviewsByProduct(productId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<ReviewDto> getReviewByUserId(long userId) {
        return reviewRepository.findByAuthorId(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<ReviewDto> getAllReviews() {
        return reviewRepository.findAll(Sort.by(Sort.Direction.DESC, "timeOfReview"))
                .stream()
                .map(this::toDto)
                .toList();
    }

    private ReviewDto toDto(ReviewEntity review) {
        return new ReviewDto(
                review.getId(),
                review.getProductId(),
                review.getAuthorId(),
                review.getRating(),
                review.getText(),
                review.getTimeOfReview()
        );
    }

    private ReviewEntity toEntity(OrderDto order, ReviewCreateDto dto) {
        ReviewEntity entity = new ReviewEntity();
        entity.setProductId(order.productId());
        entity.setAuthorId(order.customerId());
        entity.setRating(dto.rating());
        entity.setText(dto.text());
        entity.setOrderId(order.id());
        entity.setTimeOfReview(ZonedDateTime.now());
        return entity;
    }
}
