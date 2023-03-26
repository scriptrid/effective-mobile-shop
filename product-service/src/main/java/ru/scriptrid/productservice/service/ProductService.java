package ru.scriptrid.productservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.scriptrid.common.dto.OrganizationDto;
import ru.scriptrid.common.dto.ProductDto;
import ru.scriptrid.common.exception.DeletedOrganizationException;
import ru.scriptrid.common.exception.FrozenOrganizationException;
import ru.scriptrid.common.exception.InvalidOwnerException;
import ru.scriptrid.common.exception.OrganizationNotFoundByIdException;
import ru.scriptrid.common.security.JwtAuthenticationToken;
import ru.scriptrid.productservice.exceptions.*;
import ru.scriptrid.productservice.model.dto.ProductCreateDto;
import ru.scriptrid.productservice.model.dto.RequestDto;
import ru.scriptrid.productservice.model.entity.ProductEntity;
import ru.scriptrid.productservice.model.entity.RequestNewProductEntity;
import ru.scriptrid.productservice.repository.ProductRepository;
import ru.scriptrid.productservice.repository.RequestNewProductRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final WebOrganizationService webOrganizationService;
    private final RequestNewProductRepository requestNewProductRepository;

    public ProductService(ProductRepository productRepository, WebOrganizationService webOrganizationService,
                          RequestNewProductRepository requestNewProductRepository) {
        this.productRepository = productRepository;
        this.webOrganizationService = webOrganizationService;
        this.requestNewProductRepository = requestNewProductRepository;
    }


    @Transactional
    public RequestDto addRequest(JwtAuthenticationToken token, ProductCreateDto dto) {
        OrganizationDto organizationDto = webOrganizationService.getDto(dto.organizationId());
        if (organizationDto == null) {
            log.warn("Organization with id \"{}\" was not found", dto.organizationId());
            throw new OrganizationNotFoundByIdException(dto.organizationId());
        }
        if (organizationDto.isFrozen()) {
            log.warn("Organization with id \"{}\" is frozen", dto.organizationId());
            throw new FrozenOrganizationException(dto.organizationId());
        }
        if (organizationDto.isDeleted()) {
            log.warn("Organization with id \"{}\" is frozen", dto.organizationId());
            throw new DeletedOrganizationException(dto.organizationId());
        }
        if (organizationDto.ownerId() != token.getId()) {
            log.warn("User \"{}\" is not an owner of organization with id \"{}\"", token.getUsername(), dto.organizationId());
            throw new InvalidOwnerException(organizationDto.id(), organizationDto.ownerId(), token.getId());
        }

        RequestNewProductEntity request = requestNewProductRepository.save(toRequestEntity(dto));
        return toRequestDto(request);
    }

    private RequestDto toRequestDto(RequestNewProductEntity request) {
        return new RequestDto(
                request.getId(),
                request.getProductName(),
                request.getDescription(),
                request.getOrganizationId(),
                request.getPrice(),
                request.getQuantityInStock(),
                List.copyOf(request.getTags()),
                request.getSpecs()
        );
    }

    private RequestNewProductEntity toRequestEntity(ProductCreateDto dto) {
        RequestNewProductEntity entity = new RequestNewProductEntity();
        entity.setProductName(dto.productName());
        entity.setDescription(dto.description());
        entity.setOrganizationId(dto.organizationId());
        entity.setPrice(dto.price());
        entity.setQuantityInStock(dto.quantityInStock());
        entity.setTags(Set.copyOf(dto.tags()));
        entity.setSpecs(dto.specs());
        return entity;
    }

    @Transactional
    public void rejectRequest(long id) {
        requestNewProductRepository.delete(getRequestById(id));
    }

    @Transactional
    public ProductDto addProduct(long requestId) {
        RequestNewProductEntity request = getRequestById(requestId);
        if (productRepository.existsByProductName(request.getProductName())) {
            log.warn("Product with name \"{}\" already exists", request.getProductName());
            throw  new ProductAlreadyExistsByNameException(request.getProductName());
        }
        requestNewProductRepository.delete(request);
        ProductEntity newProduct = createEntity(request);

        requestNewProductRepository.delete(request);
        return toProductDto(productRepository.save(newProduct));
    }

    @Transactional
    public ProductDto editProduct(JwtAuthenticationToken token, long id, ProductCreateDto dto) {
        ProductEntity product = productRepository.findById(id).orElseThrow(
                () -> {
                    log.warn("The product to be edited with id \"{}\" was not found", id);
                    return new ProductNotFoundByIdException(id);
                }
        );

        OrganizationDto newOrganizationDto = webOrganizationService.getDto(dto.organizationId());
        OrganizationDto oldOrganizationDto = webOrganizationService.getDto(product.getId());
        if (newOrganizationDto == null) {
            log.warn("Organization \"{}\" was not found", dto.organizationId());
            throw new OrganizationNotFoundByIdException(id);
        }
        if (newOrganizationDto.isFrozen()) {
            log.warn("Organization with id \"{}\" is frozen", dto.organizationId());
            throw new FrozenOrganizationException(dto.organizationId());
        }
        if (token.isAdmin()) {
            return toProductDto(modifyEntity(product, dto));
        }

        if (oldOrganizationDto.ownerId() != token.getId()) {
            log.warn("User \"{}\" is not an owner of old organization\"{}\"", token.getUsername(), oldOrganizationDto.id());
            throw new InvalidOwnerException(oldOrganizationDto.id(), oldOrganizationDto.ownerId(), token.getId());
        }
        if (newOrganizationDto.ownerId() != token.getId()) {
            log.warn("User \"{}\" is not an owner of new organization \"{}\"", token.getUsername(), dto.organizationId());
            throw new InvalidOwnerException(newOrganizationDto.id(), newOrganizationDto.ownerId(), token.getId());
        }
        if (productRepository.existsByProductName(dto.productName()) && !product.getProductName().equals(dto.productName())) {
            log.warn("The product with new name \"{}\" already exists", dto.productName());
            throw new ProductAlreadyExistsByIdException(dto.productName());
        }

        return toProductDto(modifyEntity(product, dto));
    }

    @Transactional
    public void deleteProduct(JwtAuthenticationToken token, long id) {
        ProductEntity product = getProductEntity(id);
        OrganizationDto organizationDto = webOrganizationService.getDto(product.getOrganizationId());
        if (token.isAdmin()) {
            productRepository.deleteById(id);
        }
        if (organizationDto.ownerId() != token.getId()) {
            log.warn("User \"{}\" is not an owner of organization \"{}\"", token.getUsername(), organizationDto.id());
            throw new InvalidOwnerException(organizationDto.id(), organizationDto.ownerId(), token.getId());
        }
        productRepository.deleteById(id);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void reserveProduct(long id, int quantity) {
        ProductEntity productEntity = getProductEntity(id);
        if (productEntity.getQuantityInStock() < quantity) {
            log.warn("Insufficient quantity of product by id \"{}\"", id);
            throw new InsufficientQuantityException(productEntity.getQuantityInStock(), quantity);
        }
        productEntity.setQuantityInStock(productEntity.getQuantityInStock() - quantity);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void returnProduct(long id, int quantity) {
        ProductEntity productEntity = getProductEntity(id);
        productEntity.setQuantityInStock(productEntity.getQuantityInStock() + quantity);
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

    private ProductEntity modifyEntity(ProductEntity entity, ProductCreateDto dto) {
        entity.setProductName(dto.productName());
        entity.setDescription(dto.description());
        entity.setOrganizationId(dto.organizationId());
        entity.setPrice(dto.price());
        entity.setQuantityInStock(dto.quantityInStock());
        entity.setTags(Set.copyOf(dto.tags()));
        entity.setSpecs(dto.specs());
        return entity;
    }

    public ProductDto getProductDto(long id) {
        ProductEntity product = getProductEntity(id);
        OrganizationDto organizationDto = webOrganizationService.getDto(product.getOrganizationId());
        if (organizationDto.isFrozen()) {
            log.warn("Organization with id \"{}\" is frozen", organizationDto.id());
            throw new FrozenOrganizationException(organizationDto.id());
        }
        if (organizationDto.isDeleted()) {
            log.warn("Organization with id \"{}\" is deleted", organizationDto.id());
            throw new DeletedOrganizationException(organizationDto.id());
        }
        return toProductDto(product);

    }

    private ProductEntity getProductEntity(long id) {
        return productRepository.findById(id).orElseThrow(
                () -> {
                    log.warn("The product with id \"{}\" was not found", id);
                    return new ProductNotFoundByIdException(id);
                }
        );
    }

    private static ProductEntity createEntity(RequestNewProductEntity request) {
        ProductEntity newProduct = new ProductEntity();
        newProduct.setProductName(request.getProductName());
        newProduct.setDescription(request.getDescription());
        newProduct.setPrice(request.getPrice());
        newProduct.setQuantityInStock(request.getQuantityInStock());
        newProduct.setSpecs(request.getSpecs());
        newProduct.setOrganizationId(request.getOrganizationId());
        newProduct.setTags(request.getTags());
        return newProduct;
    }

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll() //TODO more efficient filter
                .stream()
                .filter(product -> {
                    OrganizationDto organization = webOrganizationService.getDto(product.getOrganizationId());
                    return !organization.isDeleted() && !organization.isFrozen();
                })
                .map(this::toProductDto)
                .sorted(Comparator.comparing(ProductDto::productName))
                .toList();
    }

    public List<RequestDto> getAllRequests() {
        return requestNewProductRepository
                .findAll()
                .stream()
                .map(this::toRequestDto).toList();
    }

    public RequestDto getRequest(long id) {
        return toRequestDto(getRequestById(id));
    }

    private RequestNewProductEntity getRequestById(long id) {
        return requestNewProductRepository.findById(id).orElseThrow(
                () -> {
                    log.warn("The request with id \"{}\" was not found", id);
                    return new RequestNotFoundException(id);
                }
        );
    }
}
