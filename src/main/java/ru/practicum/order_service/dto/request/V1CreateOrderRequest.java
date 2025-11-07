package ru.practicum.order_service.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class V1CreateOrderRequest {
    @NotEmpty(message = "Orders cannot be empty")
    @Valid
    private List<Order> orders;

    @Getter
    @Setter
    public static class Order {

        @NotNull(message = "Customer ID is required")
        @Positive(message = "Customer ID must be positive")
        private Long customerId;

        @NotBlank(message = "Delivery address is required")
        private String deliveryAddress;

        @NotNull(message = "Total price cents is required")
        @Positive(message = "Total price cents must be positive")
        private Long totalPriceCents;

        @NotBlank(message = "Total price currency is required")
        @Size(min = 3, max = 3, message = "Currency must be 3 characters")
        private String totalPriceCurrency;

        @NotEmpty(message = "Order items cannot be empty")
        @Valid
        private List<OrderItem> orderItems;
    }

    @Getter
    @Setter
    public static class OrderItem {

        @NotNull(message = "Product ID is required")
        @Positive(message = "Product ID must be positive")
        private Long productId;

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        private Integer quantity;

        @NotBlank(message = "Product title is required")
        private String productTitle;

        @NotBlank(message = "Product URL is required")
        private String productUrl;

        @NotNull(message = "Price cents is required")
        @Positive(message = "Price cents must be positive")
        private Long priceCents;

        @NotBlank(message = "Price currency is required")
        @Size(min = 3, max = 3, message = "Currency must be 3 characters")
        private String priceCurrency;
    }
}