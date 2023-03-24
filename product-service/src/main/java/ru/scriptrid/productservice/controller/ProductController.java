package ru.scriptrid.productservice.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.scriptrid.common.security.JwtAuthenticationToken;
import ru.scriptrid.productservice.model.dto.ProductCreateDto;
import ru.scriptrid.productservice.model.dto.ProductDto;
import ru.scriptrid.productservice.service.ProductService;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/new")
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
}
