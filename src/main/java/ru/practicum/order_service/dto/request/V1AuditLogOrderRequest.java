package ru.practicum.order_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class V1AuditLogOrderRequest {
    private List<LogOrder> orders;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LogOrder {
        private Long orderId;
        private Long orderItemId;
        private Long customerId;
        private String orderStatus;
    }
}
