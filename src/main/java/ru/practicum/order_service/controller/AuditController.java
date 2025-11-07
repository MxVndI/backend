package ru.practicum.order_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.order_service.dto.request.V1AuditLogOrderRequest;
import ru.practicum.order_service.service.AuditLogService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditLogService auditLogService;

    @PostMapping("/log-order")
    public ResponseEntity<Void> logOrders(@Valid @RequestBody V1AuditLogOrderRequest request) {
        request.getOrders().forEach(order ->
                auditLogService.logOrder(
                        order.getOrderId(),
                        order.getOrderItemId(),
                        order.getCustomerId(),
                        order.getOrderStatus()
                )
        );
        return ResponseEntity.ok().build();
    }
}
