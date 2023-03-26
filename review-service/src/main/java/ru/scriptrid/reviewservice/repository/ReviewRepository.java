package ru.scriptrid.reviewservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.scriptrid.reviewservice.model.entity.ReviewEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    boolean existsByAuthorIdAndOrderId(Long authorId, Long orderId);

    @Query("select r from ReviewEntity r where r.authorId = ?1")
    List<ReviewEntity> findByAuthorId(Long authorId);

    Optional<ReviewEntity> findByOrderId(Long orderId);

    @Query("select r from ReviewEntity r where r.productId = ?1 order by r.timeOfReview DESC")
    List<ReviewEntity> getReviewsByProduct(Long productId);
}