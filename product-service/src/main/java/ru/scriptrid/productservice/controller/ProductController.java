package ru.scriptrid.productservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.scriptrid.common.security.JwtAuthenticationToken;
import ru.scriptrid.productservice.model.dto.ProductCreateDto;
import ru.scriptrid.common.dto.ProductDto;
import ru.scriptrid.productservice.service.ProductService;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/")
    public ResponseEntity<ProductDto> newProduct(@AuthenticationPrincipal JwtAuthenticationToken token,
                                                 @RequestBody @Valid ProductCreateDto dto) {
        ProductDto productDto = productService.addProduct(token, dto);
        return ResponseEntity.ok(productDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> editProduct(@AuthenticationPrincipal JwtAuthenticationToken token,
                                                  @PathVariable long id,
                                                  @RequestBody ProductCreateDto dto) {
        ProductDto productDto = productService.editProduct(token, id, dto);
        return ResponseEntity.ok(productDto);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@AuthenticationPrincipal JwtAuthenticationToken token,
                                              @PathVariable long id) {
        productService.deleteProduct(token, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable long id) {
        ProductDto dto = productService.getProductDto(id);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasAuthority('ROLE_SERVICE')")
    @PutMapping("/{id}/reserve")
    public void reserveProduct(@PathVariable long id, @RequestBody @Valid @Min(1) int quantity) {
        productService.reserveProduct(id, quantity);
    }

    @PreAuthorize("hasAuthority('ROLE_SERVICE')")
    @PutMapping("/{id}/return")
    public void returnProduct(@PathVariable long id, @RequestBody @Valid @Min(1) int quantity) {
        productService.returnProduct(id, quantity);
    }

}
