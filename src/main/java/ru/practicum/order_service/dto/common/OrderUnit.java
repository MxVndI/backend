package ru.practicum.order_service.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderUnit {

    private Long id;
    private Long customerId;
    private String deliveryAddress;
    private Long totalPriceCents;
    private String totalPriceCurrency;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private List<OrderItemUnit> orderItems;
}