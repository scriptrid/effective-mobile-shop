package ru.scriptrid.productservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.scriptrid.common.dto.OrganizationDto;
import ru.scriptrid.common.exception.FrozenOrganizationException;
import ru.scriptrid.common.exception.InvalidOwnerException;
import ru.scriptrid.common.exception.OrganizationNotFoundByIdException;
import ru.scriptrid.common.security.JwtAuthenticationToken;
import ru.scriptrid.productservice.exceptions.ProductAlreadyExistsException;
import ru.scriptrid.productservice.exceptions.ProductNotFoundByIdException;
import ru.scriptrid.productservice.model.dto.ProductCreateDto;
import ru.scriptrid.productservice.model.dto.ProductDto;
import ru.scriptrid.productservice.model.entity.ProductEntity;
import ru.scriptrid.productservice.repository.ProductRepository;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final WebOrganizationService webOrganizationService;

    public ProductService(ProductRepository productRepository, WebOrganizationService webOrganizationService) {
        this.productRepository = productRepository;
        this.webOrganizationService = webOrganizationService;
    }


    @Transactional
    public ProductDto addProduct(JwtAuthenticationToken token, ProductCreateDto dto) {
        OrganizationDto organizationDto = webOrganizationService.getDto(dto.organizationId());
        if (organizationDto == null) {
            log.info("Organization with id \"{}\" was not found", dto.organizationId());
            throw new OrganizationNotFoundByIdException(dto.organizationId());
        }
        if (organizationDto.isFrozen()) {
            log.info("Organization with id \"{}\" is frozen", dto.organizationId());
            throw new FrozenOrganizationException();
        }
        if (!organizationDto.owner().equals(token.getUsername())) {
            log.info("User \"{}\" is not an owner of organization with id \"{}\"", token.getUsername(), dto.organizationId());
            throw new InvalidOwnerException(organizationDto.id(), organizationDto.owner(), token.getUsername());
        }
        if (productRepository.existsByProductName(dto.productName())) {
            log.info("The product \"{}\" already exists", dto.productName());
            throw new ProductAlreadyExistsException(dto.productName());
        }
        productRepository.save(toProductEntity(dto));

        return toProductDto(productRepository.findByProductName(dto.productName()));
    }

    @Transactional
    public ProductDto editProduct(JwtAuthenticationToken token, long id, ProductCreateDto dto) {
        ProductEntity product = productRepository.findById(id).orElseThrow(
                () -> {
                    log.info("The product to be edited with id \"{}\" was not found", id);
                    throw new ProductNotFoundByIdException(id);
                }
        );

        OrganizationDto newOrganizationDto = webOrganizationService.getDto(dto.organizationId());
        OrganizationDto oldOrganizationDto = webOrganizationService.getDto(product.getId());
        if (token.isAdmin()) {

            return toProductDto(modifyEntity(product, dto));
        }
        if (newOrganizationDto == null) {
            log.info("Organization \"{}\" was not found", dto.organizationId());
            throw new OrganizationNotFoundByIdException(id);
        }
        if (newOrganizationDto.isFrozen()) {
            log.info("Organization with id \"{}\" is frozen", dto.organizationId());
            throw new FrozenOrganizationException();
        }

        if (!oldOrganizationDto.owner().equals(token.getUsername())) {
            log.info("User \"{}\" is not an owner of old organization\"{}\"", token.getUsername(), oldOrganizationDto.id());
            throw new InvalidOwnerException(oldOrganizationDto.id(), oldOrganizationDto.owner(), token.getUsername());
        }
        if (!newOrganizationDto.owner().equals(token.getUsername())) {
            log.info("User \"{}\" is not an owner of new organization \"{}\"", token.getUsername(), dto.organizationId());
            throw new InvalidOwnerException(newOrganizationDto.id(), newOrganizationDto.owner(), token.getUsername());
        }
        if (productRepository.existsByProductName(dto.productName()) && !product.getProductName().equals(dto.productName())) {
            log.info("The product with new name \"{}\" already exists", dto.productName());
            throw new ProductAlreadyExistsException(dto.productName());
        }

        return toProductDto(modifyEntity(product, dto));
    }

    @Transactional
    public void deleteProduct(JwtAuthenticationToken token, long id) {
        ProductEntity product = productRepository.findById(id).orElseThrow(
                () -> {
                    log.info("The product to be edited with id \"{}\" was not found", id);
                    throw new ProductNotFoundByIdException(id);
                }
        );
        OrganizationDto organizationDto = webOrganizationService.getDto(product.getOrganizationId());
        if (token.isAdmin()) {
            productRepository.deleteById(id);
        }
        if (!organizationDto.owner().equals(token.getUsername())) {
            log.info("User \"{}\" is not an owner of organization \"{}\"", token.getUsername(), organizationDto.id());
            throw new InvalidOwnerException(organizationDto.id(), organizationDto.owner(), token.getUsername());
        }
        productRepository.deleteById(id);
    }

    public ProductDto getProductDto(long id) {
        ProductEntity product = productRepository.findById(id).orElseThrow(
                () -> {
                    log.info("The product to be edited with id \"{}\" was not found", id);
                    throw new ProductNotFoundByIdException(id);
                }
        );
        return toProductDto(product);

    }

    private ProductDto toProductDto(ProductEntity entity) {
        return new ProductDto(
                entity.getId(),
                entity.getProductName(),
                entity.getDescription(),
                entity.getOrganizationId(),
                entity.getPrice(),
                entity.getQuantityInStock(),
                List.copyOf(entity.getTags()),
                entity.getSpecs()
        );
    }

    private ProductEntity toProductEntity(ProductCreateDto dto) {
        ProductEntity entity = new ProductEntity();
        modifyEntity(entity, dto);
        return entity;
    }

    private ProductEntity modifyEntity(ProductEntity entity, ProductCreateDto dto) {
        entity.setProductName(dto.productName());

        if (dto.description() != null) {
            entity.setDescription(dto.description());
        } else {
            entity.setDescription("Empty description");
        }

        entity.setOrganizationId(dto.organizationId());
        entity.setPrice(dto.price());
        entity.setQuantityInStock(dto.quantityInStock());
        entity.setTags(Set.copyOf(dto.tags()));
        if (dto.specs() != null) {
            entity.setSpecs(dto.specs());
        } else {
            entity.setSpecs("Empty specs");
        }
        return entity;
    }
}
