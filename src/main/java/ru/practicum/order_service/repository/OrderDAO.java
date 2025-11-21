package ru.practicum.order_service.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.practicum.order_service.model.Order;
import ru.practicum.order_service.model.OrderItem;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class OrderDAO {
    private final JdbcTemplate jdbcTemplate;
    private final OrderItemDAO orderItemDAO;

    public OrderDAO(JdbcTemplate jdbcTemplate, OrderItemDAO orderItemDAO) {
        this.jdbcTemplate = jdbcTemplate;
        this.orderItemDAO = orderItemDAO;
    }

    public List<Order> findByCustomerIds(List<Long> customerIds, int limit, int offset) {
        if (customerIds.isEmpty()) return List.of();

        String sql = "SELECT * FROM orders WHERE customer_id IN (" +
                getPlaceholders(customerIds.size()) + ") ORDER BY created_at DESC LIMIT ? OFFSET ?";

        List<Object> params = new ArrayList<>(customerIds);
        params.add(limit);
        params.add(offset);

        return jdbcTemplate.query(sql, params.toArray(), getOrderRowMapper());
    }

    public List<Order> findByIdIn(List<Long> ids, int limit, int offset) {
        if (ids.isEmpty()) return List.of();

        String sql = "SELECT * FROM orders WHERE id IN (" +
                getPlaceholders(ids.size()) + ") ORDER BY created_at DESC LIMIT ? OFFSET ?";

        List<Object> params = new ArrayList<>(ids);
        params.add(limit);
        params.add(offset);

        return jdbcTemplate.query(sql, params.toArray(), getOrderRowMapper());
    }

    public List<Order> findByIdInWithItems(List<Long> ids) {
        if (ids.isEmpty()) return List.of();

        String orderSql = "SELECT * FROM orders WHERE id IN (" + getPlaceholders(ids.size()) + ") ORDER BY created_at DESC";
        List<Order> orders = jdbcTemplate.query(orderSql, ids.toArray(), getOrderRowMapper());

        List<Long> orderIds = orders.stream().map(Order::getId).toList();
        List<OrderItem> items = orderItemDAO.findByOrderIds(orderIds);

        Map<Long, List<OrderItem>> itemsByOrderId = items.stream()
                .collect(Collectors.groupingBy(item -> item.getOrder().getId()));

        orders.forEach(order -> {
            List<OrderItem> orderItems = itemsByOrderId.getOrDefault(order.getId(), List.of());
            orderItems.forEach(item -> item.setOrder(order));
            order.setOrderItems(orderItems);
        });

        return orders;
    }

    public void saveAll(List<Order> orders) {
        if (orders.isEmpty()) return;

        String sql = """
            INSERT INTO orders (customer_id, delivery_address, total_price_cents, total_price_currency, created_at, updated_at) 
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        jdbcTemplate.batchUpdate(sql, orders, orders.size(), (ps, order) -> {
            ps.setLong(1, order.getCustomerId());
            ps.setString(2, order.getDeliveryAddress());
            ps.setLong(3, order.getTotalPriceCents());
            ps.setString(4, order.getTotalPriceCurrency());
            ps.setTimestamp(5, Timestamp.valueOf(order.getCreatedAt().toLocalDateTime()));
            ps.setTimestamp(6, Timestamp.valueOf(order.getUpdatedAt().toLocalDateTime()));
        });
    }

    private RowMapper<Order> getOrderRowMapper() {
        return (rs, rowNum) -> {
            Order order = new Order();
            order.setId(rs.getLong("id"));
            order.setCustomerId(rs.getLong("customer_id"));
            order.setDeliveryAddress(rs.getString("delivery_address"));
            order.setTotalPriceCents(rs.getLong("total_price_cents"));
            order.setTotalPriceCurrency(rs.getString("total_price_currency"));
            order.setCreatedAt(rs.getTimestamp("created_at").toInstant().atOffset(java.time.ZoneOffset.UTC));
            order.setUpdatedAt(rs.getTimestamp("updated_at").toInstant().atOffset(java.time.ZoneOffset.UTC));
            order.setOrderItems(new ArrayList<>());
            return order;
        };
    }

    private String getPlaceholders(int count) {
        return String.join(",", Collections.nCopies(count, "?"));
    }

    public List<Order> findAll(int limit, int offset) {
        String sql = "SELECT * FROM orders ORDER BY created_at DESC LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, getOrderRowMapper(), limit, offset);
    }
}