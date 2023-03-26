package ru.scriptrid.reviewservice.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.scriptrid.common.security.JwtAuthenticationToken;
import ru.scriptrid.reviewservice.model.dto.OrderCreateDto;
import ru.scriptrid.reviewservice.model.dto.OrderDto;
import ru.scriptrid.reviewservice.service.OrderService;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/order/")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public OrderDto addOrder(@AuthenticationPrincipal JwtAuthenticationToken token, @RequestBody OrderCreateDto dto) {
        return orderService.addOrder(dto, token);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public List<OrderDto> getOrders(@RequestParam(required = false) Long userId) {
        return orderService.getOrders(userId);
    }

    @GetMapping("/my")
    public List<OrderDto> getUserOrders(@AuthenticationPrincipal JwtAuthenticationToken token) {
        return orderService.getOrders(token.getId());
    }

    @PutMapping("/{orderId}/refund")
    public OrderDto refundOrder(@AuthenticationPrincipal JwtAuthenticationToken token, @PathVariable long orderId) {
        return orderService.refundOrder(token.getId(), orderId, ZonedDateTime.now());
    }
}
