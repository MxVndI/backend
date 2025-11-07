package ru.practicum.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedMessage {
    private Long id;
    private Long customerId;
    private String deliveryAddress;
    private Long totalPriceCents;
    private String totalPriceCurrency;
    private List<OrderItemMessage> orderItems;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemMessage {
        private Long id;
        private Long productId;
        private String productTitle;
        private String productUrl;
        private Integer quantity;
        private Long priceCents;
        private String priceCurrency;
    }
}