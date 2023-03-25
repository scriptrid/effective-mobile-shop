package ru.scriptrid.orderservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.scriptrid.common.security.JwtAuthenticationToken;
import ru.scriptrid.orderservice.model.dto.OrderCreateDto;
import ru.scriptrid.orderservice.model.dto.OrderDto;

@Service
@Slf4j
public class OrderService {

    private final WebProductService webProductService;

    public OrderService(WebProductService webProductService) {
        this.webProductService = webProductService;
    }

    @Transactional
    public OrderDto addOrder(JwtAuthenticationToken token, OrderCreateDto dto) {
        webProductService.reserveProduct(dto.id());
    }
}
