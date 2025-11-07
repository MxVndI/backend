package ru.practicum.order_service.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.order_service.dto.OrderCreatedMessage;
import ru.practicum.order_service.dto.common.OrderUnit;
import ru.practicum.order_service.mapper.OrderMapper;
import ru.practicum.order_service.mapper.OrderMessageMapper;
import ru.practicum.order_service.model.Order;
import ru.practicum.order_service.model.OrderItem;
import ru.practicum.order_service.rabbit.RabbitMqPublisher;
import ru.practicum.order_service.repository.OrderItemRepository;
import ru.practicum.order_service.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMessageMapper orderMessageMapper;
    private final OrderMapper orderMapper;
    private final RabbitMqPublisher rabbitMqPublisher;

    @Transactional
    public List<OrderUnit> batchInsert(List<Order> orders) {
        List<Order> savedOrders = orderRepository.saveAll(orders);

        List<OrderCreatedMessage> messages = savedOrders.stream()
                .map(orderMessageMapper::toOrderCreatedMessage)
                .toList();

        rabbitMqPublisher.publishOrderCreated(messages);

        return orderMapper.toOrderUnitList(savedOrders);
    }


    @Transactional(readOnly = true)
    public List<OrderUnit> getOrders(List<Long> ids, List<Long> customerIds,
                                     int page, int pageSize, boolean includeOrderItems) {
        Pageable pageable = PageRequest.of(page, pageSize);
        List<Order> orders;

        if (ids != null && !ids.isEmpty()) {
            if (includeOrderItems) {
                orders = orderRepository.findByIdInWithItems(ids);
            } else {
                orders = orderRepository.findByIdIn(ids, pageable).getContent();
            }
        } else if (customerIds != null && !customerIds.isEmpty()) {
            orders = orderRepository.findByCustomerIds(customerIds, pageable).getContent();
        } else {
            orders = orderRepository.findAll(pageable).getContent();
        }

        if (includeOrderItems && (ids == null || ids.isEmpty())) {
            loadOrderItems(orders);
        }

        return orderMapper.toOrderUnitList(orders);
    }

    private void loadOrderItems(List<Order> orders) {
        List<Long> orderIds = orders.stream().map(Order::getId).toList();
        Map<Long, List<OrderItem>> itemsByOrderId = orderItemRepository.findByOrderIds(orderIds)
                .stream()
                .collect(Collectors.groupingBy(oi -> oi.getOrder().getId()));

        orders.forEach(order ->
                order.setOrderItems(itemsByOrderId.getOrDefault(order.getId(), List.of())));
    }

    private void validateOrder(Order order) {
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
