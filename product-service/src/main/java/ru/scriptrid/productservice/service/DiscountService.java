package ru.scriptrid.productservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.scriptrid.productservice.exceptions.DiscountNotFoundException;
import ru.scriptrid.productservice.exceptions.EndedDiscountException;
import ru.scriptrid.productservice.exceptions.InvalidProductsException;
import ru.scriptrid.productservice.exceptions.InvalidTimeException;
import ru.scriptrid.productservice.model.dto.DiscountCreateDto;
import ru.scriptrid.productservice.model.dto.DiscountDto;
import ru.scriptrid.productservice.model.entity.DiscountEntity;
import ru.scriptrid.productservice.model.entity.ProductEntity;
import ru.scriptrid.productservice.repository.DiscountRepository;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DiscountService {

    private final ProductService productService;
    private final DiscountRepository discountRepository;

    public DiscountService(ProductService productService, DiscountRepository discountRepository) {
        this.productService = productService;
        this.discountRepository = discountRepository;
    }

    @Transactional
    public DiscountDto addDiscount(DiscountCreateDto dto) {
        validateStartTimeIsNotInPast(dto);
        validateEndTimeIsNotInPast(dto);
        validateDtoTimeEndsAfterStart(dto);
        DiscountEntity discount = discountRepository.save(toEntity(dto));
        log.info("Discount with id \"{}\" was successfully added", discount.getId());
        return toDto(discount);
    }


    @Transactional
    public DiscountDto editDiscount(long id, DiscountCreateDto dto) {
        DiscountEntity discount = getDiscountEntity(id);
        validateDiscountNotEnded(discount);
        validateDtoTimeEndsAfterStart(dto);
        modifyDiscount(discount, dto);
        log.info("Discount with id \"{}\" was successfully edited", discount.getId());
        return toDto(discount);
    }

    private void modifyDiscount(DiscountEntity discount, DiscountCreateDto dto) {
        Set<ProductEntity> productsForDiscount = productService.getProductsByIds(dto.productIds());
        if (productsForDiscount.size() != dto.productIds().size()) {
            throw new InvalidProductsException(dto.productIds());
        }
        discount.getProducts().clear();
        discount.getProducts().addAll(productsForDiscount);
        discount.setPriceModifier(dto.priceModifier());

        if (dto.discountStart() == null) {
            discount.setDiscountStart(ZonedDateTime.now());
        } else {
            discount.setDiscountStart(dto.discountStart());
        }
        discount.setDiscountEnd(dto.discountEnd());
    }

    private DiscountEntity getDiscountEntity(long id) {
        return discountRepository.findById(id).orElseThrow(() -> {
            log.warn("Discount with id \"{}\" not found", id);
            return new DiscountNotFoundException(id);
        });
    }

    @Transactional
    public void stopDiscount(long id) {
        DiscountEntity discountEntity = getDiscountEntity(id);
        if (!discountStarted(discountEntity)) {
            discountRepository.delete(discountEntity);
        } else {
            discountEntity.setDiscountEnd(ZonedDateTime.now());
        }
    }

    private boolean discountStarted(DiscountEntity discountEntity) {
        return discountEntity.getDiscountStart().isBefore(ZonedDateTime.now());
    }

    private void validateDiscountNotEnded(DiscountEntity discount) {
        if (discount.getDiscountEnd() != null
                && discount.getDiscountEnd().isBefore(ZonedDateTime.now())) {
            log.warn("Discount to be edited with id \"{}\" ended", discount.getId());
            throw new EndedDiscountException(discount.getId());
        }
    }

    private void validateDtoTimeEndsAfterStart(DiscountCreateDto dto) {
        if (dto.discountStart() != null && dto.discountEnd() != null && dto.discountEnd().isBefore(dto.discountStart())) {
            log.warn("Discount ends earlier than starts: {}", dto.discountEnd());
            throw new InvalidTimeException(dto.discountEnd());
        }
    }

    private void validateEndTimeIsNotInPast(DiscountCreateDto dto) {
        if (dto.discountEnd() != null && dto.discountEnd().isBefore(ZonedDateTime.now())) {
            log.warn("Discount ends in the past: {}", dto.discountEnd());
            throw new InvalidTimeException(dto.discountEnd());
        }
    }

    private void validateStartTimeIsNotInPast(DiscountCreateDto dto) {
        if (dto.discountStart() != null && dto.discountStart().isBefore(ZonedDateTime.now())) {
            log.warn("Discount starts in the past: {}", dto.discountStart());
            throw new InvalidTimeException(dto.discountStart());
        }
    }

    private DiscountEntity toEntity(DiscountCreateDto dto) {
        DiscountEntity entity = new DiscountEntity();
        modifyDiscount(entity, dto);

        return entity;
    }

    private DiscountDto toDto(DiscountEntity discount) {
        return new DiscountDto(
                discount.getId(),
                discount.getProducts()
                        .stream()
                        .map(ProductEntity::getId)
                        .collect(Collectors.toSet()),
                discount.getPriceModifier(),
                discount.getDiscountStart(),
                discount.getDiscountEnd()
        );
    }
}
