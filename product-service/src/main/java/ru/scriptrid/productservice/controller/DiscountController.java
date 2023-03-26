package ru.scriptrid.productservice.controller;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.scriptrid.productservice.model.dto.DiscountCreateDto;
import ru.scriptrid.productservice.model.dto.DiscountDto;
import ru.scriptrid.productservice.service.DiscountService;

@RestController
@RequestMapping("/api/discount/")
public class DiscountController {

    private final DiscountService discountService;

    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping()
    public DiscountDto addDiscount(@RequestBody @Valid DiscountCreateDto dto) {
        return discountService.addDiscount(dto);
    }



}
