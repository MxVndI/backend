package ru.practicum.order_service.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.order_service.dto.request.V1AuditLogOrderRequest;
import ru.practicum.order_service.service.AuditLogService;

@Service
@RequiredArgsConstructor
public class OmsClient {

    private final AuditLogService auditLogService;

    public void logOrder(V1AuditLogOrderRequest request) {
        if (request.getOrders() == null || request.getOrders().isEmpty()) {
            return;
        }

        for (V1AuditLogOrderRequest.LogOrder order : request.getOrders()) {
            auditLogService.logOrder(
                    order.getOrderId(),
                    order.getOrderItemId(),
                    order.getCustomerId(),
                    order.getOrderStatus()
            );
        }
    }
}
