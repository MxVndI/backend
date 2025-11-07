package ru.practicum.order_service.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemUnit {

    private Long id;
    private Long orderId;
    private Long productId;
    private Integer quantity;
    private String productTitle;
    private String productUrl;
    private Long priceCents;
    private String priceCurrency;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}