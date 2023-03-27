package ru.scriptrid.productservice.controller;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.scriptrid.common.dto.ProductDto;
import ru.scriptrid.common.security.JwtAuthenticationToken;
import ru.scriptrid.productservice.model.dto.ProductCreateDto;
import ru.scriptrid.productservice.model.dto.RequestDto;
import ru.scriptrid.productservice.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/product/request/")
public class ProductRequestController {
    private final ProductService productService;

    public ProductRequestController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public RequestDto newRequestProduct(@AuthenticationPrincipal JwtAuthenticationToken token,
                                        @RequestBody @Valid ProductCreateDto dto) {
        return productService.addRequest(token, dto);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public List<RequestDto> getAllRequests() {
        return productService.getAllRequests();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public RequestDto getRequest(@PathVariable long id) {
        return productService.getRequest(id);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public void rejectRequest(@PathVariable long id) {
        productService.rejectRequest(id);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/{id}")
    public ProductDto acceptRequest(@PathVariable long id) {
        return productService.addProduct(id);
    }
}
