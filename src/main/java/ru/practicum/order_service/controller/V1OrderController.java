package ru.practicum.order_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.order_service.dto.common.OrderUnit;
import ru.practicum.order_service.dto.request.V1QueryOrdersRequest;
import ru.practicum.order_service.dto.respnose.V1CreateOrderResponse;
import ru.practicum.order_service.dto.respnose.V1QueryOrdersResponse;
import ru.practicum.order_service.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import ru.practicum.order_service.dto.request.V1CreateOrderRequest;
import ru.practicum.order_service.mapper.OrderMapper;
import ru.practicum.order_service.model.Order;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class V1OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @PostMapping("/batch-create")
    public ResponseEntity<V1CreateOrderResponse> batchCreate(@Valid @RequestBody V1CreateOrderRequest request) {

        List<Order> orders = orderMapper.toEntityListWithItems(request.getOrders());
        List<OrderUnit> result = orderService.batchInsert(orders);

        return ResponseEntity.ok(new V1CreateOrderResponse(result));
    }

    @PostMapping("/query")
    public ResponseEntity<V1QueryOrdersResponse> queryOrders(@Valid @RequestBody V1QueryOrdersRequest request) {

        var result = orderService.getOrders(
                request.getIds(),
                request.getCustomerIds(),
                request.getPage(),
                request.getPageSize(),
                request.getIncludeOrderItems() != null ? request.getIncludeOrderItems() : false
        );

        return ResponseEntity.ok(new V1QueryOrdersResponse(result));
    }
}