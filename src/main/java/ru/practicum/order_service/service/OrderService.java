package ru.practicum.order_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.order_service.dto.OrderCreatedMessage;
import ru.practicum.order_service.dto.common.OrderUnit;
import ru.practicum.order_service.mapper.OrderMapper;
import ru.practicum.order_service.mapper.OrderMessageMapper;
import ru.practicum.order_service.model.Order;
import ru.practicum.order_service.model.OrderItem;
import ru.practicum.order_service.rabbit.RabbitMqPublisher;
import ru.practicum.order_service.repository.OrderDAO;
import ru.practicum.order_service.repository.OrderItemDAO;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderDAO orderDAO;
    private final OrderItemDAO orderItemDAO;
    private final OrderMessageMapper orderMessageMapper;
    private final OrderMapper orderMapper;
    private final RabbitMqPublisher rabbitMqPublisher;

    @Transactional
    public List<OrderUnit> batchInsert(List<Order> orders) {
        orders.forEach(this::validateOrder);

        orderDAO.saveAll(orders);

        List<OrderItem> allItems = orders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .peek(item -> item.setOrder(item.getOrder()))
                .toList();
        orderItemDAO.saveAll(allItems);

        List<OrderCreatedMessage> messages = orders.stream()
                .map(orderMessageMapper::toOrderCreatedMessage)
                .toList();
        rabbitMqPublisher.publishOrderCreated(messages);

        return orderMapper.toOrderUnitList(orders);
    }

    @Transactional(readOnly = true)
    public List<OrderUnit> getOrders(List<Long> ids, List<Long> customerIds,
                                     int page, int pageSize, boolean includeOrderItems) {
        int offset = page * pageSize;
        List<Order> orders;

        if (ids != null && !ids.isEmpty()) {
            orders = includeOrderItems
                    ? orderDAO.findByIdInWithItems(ids)
                    : orderDAO.findByIdIn(ids, pageSize, offset);
        } else if (customerIds != null && !customerIds.isEmpty()) {
            orders = orderDAO.findByCustomerIds(customerIds, pageSize, offset);
        } else {
            orders = orderDAO.findAll(pageSize, offset);
        }

        if (includeOrderItems && !orders.isEmpty() &&
                (ids == null || ids.isEmpty() || orders.getFirst().getOrderItems().isEmpty())) {
            loadOrderItems(orders);
        }

        return orderMapper.toOrderUnitList(orders);
    }

    private void loadOrderItems(List<Order> orders) {
        if (orders.isEmpty()) return;

        List<Long> orderIds = orders.stream().map(Order::getId).toList();
        Map<Long, List<OrderItem>> itemsByOrderId = orderItemDAO.findByOrderIds(orderIds)
                .stream()
                .collect(Collectors.groupingBy(item -> item.getOrder().getId()));

        orders.forEach(order -> {
            List<OrderItem> items = itemsByOrderId.getOrDefault(order.getId(), List.of());
            items.forEach(item -> item.setOrder(order));
            order.setOrderItems(items);
        });
    }

    private void validateOrder(Order order) {
        if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one OrderItem");
        }

        long calculatedTotal = order.getOrderItems().stream()
                .mapToLong(item -> item.getPriceCents() * item.getQuantity())
                .sum();

        if (!order.getTotalPriceCents().equals(calculatedTotal)) {
            throw new IllegalArgumentException(
                    "TotalPriceCents should be equal to sum of all OrderItems.PriceCents * OrderItems.Quantity. " +
                            "Expected: " + calculatedTotal + ", but got: " + order.getTotalPriceCents());
        }

        String firstCurrency = order.getOrderItems().getFirst().getPriceCurrency();
        boolean allSameCurrency = order.getOrderItems().stream()
                .allMatch(item -> item.getPriceCurrency().equals(firstCurrency));

        if (!allSameCurrency) {
            throw new IllegalArgumentException("All OrderItems.PriceCurrency should be the same");
        }

        if (!firstCurrency.equals(order.getTotalPriceCurrency())) {
            throw new IllegalArgumentException(
                    "OrderItems.PriceCurrency should be the same as TotalPriceCurrency");
        }
    }
}