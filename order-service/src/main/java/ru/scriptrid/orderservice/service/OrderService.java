package ru.scriptrid.orderservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.scriptrid.common.dto.OrganizationDto;
import ru.scriptrid.common.dto.ProductDto;
import ru.scriptrid.common.dto.UserDto;
import ru.scriptrid.common.security.JwtAuthenticationToken;
import ru.scriptrid.orderservice.model.dto.OrderCreateDto;
import ru.scriptrid.orderservice.model.dto.OrderDto;

import java.math.BigDecimal;

@Service
@Slf4j
public class OrderService {

    private final WebProductService webProductService;
    private final WebUserService webUserService;
    private final BigDecimal commission;
    private final WebOrganizationService webOrganizationService;

    public OrderService(WebProductService webProductService,
                        WebUserService webUserService,
                        @Value("${orders.commission}") BigDecimal commission, WebOrganizationService webOrganizationService) {
        this.webProductService = webProductService;
        this.webUserService = webUserService;
        this.commission = commission;
        this.webOrganizationService = webOrganizationService;
    }

    @Transactional
    public OrderDto addOrder(OrderCreateDto dto, JwtAuthenticationToken token) {
        ProductDto product = webProductService.getDto(dto.productId());
        OrganizationDto sellersOrganization = webOrganizationService.getDto(product.organizationId());
        UserDto seller = webUserService.getDto(sellersOrganization.ownerId());
        webProductService.reserveProduct(dto.productId());
        return addReservedOrder(dto, product,  sellersOrganization.ownerId(), token.getId());

    }

    private OrderDto addReservedOrder(OrderCreateDto dto, ProductDto product, long sellerId, long customerId) {
        try {
            BigDecimal total = product.price().multiply(BigDecimal.valueOf(dto.quantity()));
            BigDecimal sellersIncome = total.multiply(commission);
            webUserService.transferMoney(customerId, sellerId, total, sellersIncome);
        } catch (Throwable e) {
            webProductService.returnProduct(dto.productId());
            throw e;
        }
    }
}
