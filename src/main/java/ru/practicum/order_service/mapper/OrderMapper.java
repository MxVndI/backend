package ru.practicum.order_service.mapper;

import ru.practicum.order_service.dto.common.OrderItemUnit;
import ru.practicum.order_service.dto.common.OrderUnit;
import ru.practicum.order_service.dto.request.V1CreateOrderRequest;
import ru.practicum.order_service.model.Order;
import ru.practicum.order_service.model.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public Order toEntity(V1CreateOrderRequest.Order request) {
        if (request == null) {
            return null;
        }

        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setTotalPriceCents(request.getTotalPriceCents());
        order.setTotalPriceCurrency(request.getTotalPriceCurrency());

        // OrderItems будут установлены позже через отдельный метод
        return order;
    }

    public List<Order> toEntityList(List<V1CreateOrderRequest.Order> requests) {
        if (requests == null) {
            return List.of();
        }

        return requests.stream()
                .map(this::toEntity)
                .toList();
    }

    public OrderItem toOrderItemEntity(V1CreateOrderRequest.OrderItem request) {
        if (request == null) {
            return null;
        }

        OrderItem orderItem = new OrderItem();
        orderItem.setProductId(request.getProductId());
        orderItem.setQuantity(request.getQuantity());
        orderItem.setProductTitle(request.getProductTitle());
        orderItem.setProductUrl(request.getProductUrl());
        orderItem.setPriceCents(request.getPriceCents());
        orderItem.setPriceCurrency(request.getPriceCurrency());

        return orderItem;
    }

    public List<OrderItem> toOrderItemEntityList(List<V1CreateOrderRequest.OrderItem> requests) {
        if (requests == null) {
            return List.of();
        }

        return requests.stream()
                .map(this::toOrderItemEntity)
                .toList();
    }

    // Полное преобразование Request в Entity с установкой связей
    public Order toEntityWithItems(V1CreateOrderRequest.Order request) {
        if (request == null) {
            return null;
        }

        Order order = toEntity(request);

        if (request.getOrderItems() != null) {
            List<OrderItem> orderItems = toOrderItemEntityList(request.getOrderItems());
            orderItems.forEach(item -> item.setOrder(order));
            order.setOrderItems(orderItems);
        }

        return order;
    }

    public List<Order> toEntityListWithItems(List<V1CreateOrderRequest.Order> requests) {
        if (requests == null) {
            return List.of();
        }

        return requests.stream()
                .map(this::toEntityWithItems)
                .toList();
    }

    // Entity -> DTO

    public OrderUnit toOrderUnit(Order entity) {
        if (entity == null) {
            return null;
        }

        OrderUnit orderUnit = new OrderUnit();
        orderUnit.setId(entity.getId());
        orderUnit.setCustomerId(entity.getCustomerId());
        orderUnit.setDeliveryAddress(entity.getDeliveryAddress());
        orderUnit.setTotalPriceCents(entity.getTotalPriceCents());
        orderUnit.setTotalPriceCurrency(entity.getTotalPriceCurrency());
        orderUnit.setCreatedAt(entity.getCreatedAt());
        orderUnit.setUpdatedAt(entity.getUpdatedAt());

        if (entity.getOrderItems() != null) {
            List<OrderItemUnit> orderItemUnits = entity.getOrderItems().stream()
                    .map(this::toOrderItemUnit)
                    .toList();
            orderUnit.setOrderItems(orderItemUnits);
        } else {
            orderUnit.setOrderItems(List.of());
        }

        return orderUnit;
    }

    public List<OrderUnit> toOrderUnitList(List<Order> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(this::toOrderUnit)
                .toList();
    }

    public OrderItemUnit toOrderItemUnit(OrderItem entity) {
        if (entity == null) {
            return null;
        }

        OrderItemUnit orderItemUnit = new OrderItemUnit();
        orderItemUnit.setId(entity.getId());
        orderItemUnit.setOrderId(entity.getOrder() != null ? entity.getOrder().getId() : null);
        orderItemUnit.setProductId(entity.getProductId());
        orderItemUnit.setQuantity(entity.getQuantity());
        orderItemUnit.setProductTitle(entity.getProductTitle());
        orderItemUnit.setProductUrl(entity.getProductUrl());
        orderItemUnit.setPriceCents(entity.getPriceCents());
        orderItemUnit.setPriceCurrency(entity.getPriceCurrency());
        orderItemUnit.setCreatedAt(entity.getCreatedAt());
        orderItemUnit.setUpdatedAt(entity.getUpdatedAt());

        return orderItemUnit;
    }

    public List<OrderItemUnit> toOrderItemUnitList(List<OrderItem> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(this::toOrderItemUnit)
                .toList();
    }
}