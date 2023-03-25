package ru.scriptrid.orderservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.scriptrid.common.security.JwtAuthenticationToken;
import ru.scriptrid.orderservice.model.dto.OrderCreateDto;
import ru.scriptrid.orderservice.model.dto.OrderDto;
import ru.scriptrid.orderservice.service.OrderService;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping()
    public OrderDto addOrder(@AuthenticationPrincipal JwtAuthenticationToken token, @RequestBody OrderCreateDto dto) {
        return orderService.addOrder(dto, token);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/all")
    public List<OrderDto> getAllOrders() {
        return orderService.getAllOrders();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/user/{id}")
    public List<OrderDto> getUsersOrders(@PathVariable long id) {
        return orderService.getUsersOrders(id);
    }

    @GetMapping("/my")
    public List<OrderDto> getUsersOrders(@AuthenticationPrincipal JwtAuthenticationToken token) {
        return orderService.getUsersOrders(token.getId());
    }

    @PutMapping("/my/{id}")
    public ResponseEntity<OrderDto> refundOrder(@AuthenticationPrincipal JwtAuthenticationToken token, @PathVariable long orderId) {
        return ResponseEntity.ok(orderService.refundOrder(token.getId(), orderId, ZonedDateTime.now()));
    }
}
