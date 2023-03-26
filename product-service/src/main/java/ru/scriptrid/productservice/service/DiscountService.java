package ru.scriptrid.productservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

        if (dto.discountStart() != null && dto.discountStart().compareTo(ZonedDateTime.now()) < 0) {
            log.warn("Discount starts in the past: {}", dto.discountStart());
            throw new InvalidTimeException(dto.discountStart());
        }
        if (dto.discountEnd() != null && dto.discountEnd().compareTo(ZonedDateTime.now()) < 0) {
            log.warn("Discount ends in the past: {}", dto.discountEnd());
            throw new InvalidTimeException(dto.discountEnd());
        }
        if (dto.discountEnd() != null && dto.discountEnd().compareTo(dto.discountStart()) < 0) {
            log.warn("Discount ends earlier than starts: {}", dto.discountEnd());
            throw new InvalidTimeException(dto.discountEnd());
        }
        Set<ProductEntity> productsForDiscount = productService.getProductsByIds(dto.productIds());

        if (productsForDiscount.size() != dto.productIds().size()) {
            throw new InvalidProductsException(dto.productIds());
        }

        DiscountEntity discount = discountRepository.save(toEntity(dto, productsForDiscount));
        return toDto(discount);
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

    private DiscountEntity toEntity(DiscountCreateDto dto, Set<ProductEntity> productsForDiscount) {
        DiscountEntity entity = new DiscountEntity();
        entity.getProducts().addAll(productsForDiscount);
        entity.setPriceModifier(dto.priceModifier());

        if (dto.discountStart() == null) {
            entity.setDiscountStart(ZonedDateTime.now());
        } else {
            entity.setDiscountStart(dto.discountStart());
        }
        entity.setDiscountEnd(dto.discountEnd());

        return entity;
    }
}
