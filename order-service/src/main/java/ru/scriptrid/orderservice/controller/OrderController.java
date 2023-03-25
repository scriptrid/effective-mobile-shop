package ru.scriptrid.orderservice.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.scriptrid.common.security.JwtAuthenticationToken;
import ru.scriptrid.orderservice.model.dto.OrderCreateDto;
import ru.scriptrid.orderservice.model.dto.OrderDto;
import ru.scriptrid.orderservice.service.OrderService;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping()
    public OrderDto addOrder(@AuthenticationPrincipal JwtAuthenticationToken token, @RequestBody OrderCreateDto dto) {
        return orderService.addOrder(token, dto);
    }

}
