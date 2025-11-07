package ru.practicum.order_service.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.order_service.dto.OrderCreatedMessage;
import ru.practicum.order_service.model.Order;

import java.util.stream.Collectors;

@Component
public class OrderMessageMapper {

    public OrderCreatedMessage toOrderCreatedMessage(Order order) {
        if (order == null) return null;

        OrderCreatedMessage message = new OrderCreatedMessage();
        message.setId(order.getId());
        message.setCustomerId(order.getCustomerId());
        message.setDeliveryAddress(order.getDeliveryAddress());
        message.setTotalPriceCents(order.getTotalPriceCents());
        message.setTotalPriceCurrency(order.getTotalPriceCurrency());

        if (order.getOrderItems() != null) {
            message.setOrderItems(order.getOrderItems().stream()
                    .map(item -> {
                        var msgItem = new OrderCreatedMessage.OrderItemMessage();
                        msgItem.setId(item.getId());
                        msgItem.setProductId(item.getProductId());
                        msgItem.setProductTitle(item.getProductTitle());
                        msgItem.setProductUrl(item.getProductUrl());
                        msgItem.setQuantity(item.getQuantity());
                        msgItem.setPriceCents(item.getPriceCents());
                        msgItem.setPriceCurrency(item.getPriceCurrency());
                        return msgItem;
                    })
                    .collect(Collectors.toList()));
        }
        return message;
    }

    public java.util.List<OrderCreatedMessage> toOrderCreatedMessageList(java.util.List<Order> orders) {
        if (orders == null) return java.util.List.of();
        return orders.stream()
                .map(this::toOrderCreatedMessage)
                .collect(Collectors.toList());
    }
}
