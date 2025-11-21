package ru.practicum.order_service.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.practicum.order_service.model.Order;
import ru.practicum.order_service.model.OrderItem;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

@Repository
public class OrderItemDAO {
    private final JdbcTemplate jdbcTemplate;

    public OrderItemDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<OrderItem> findByOrderIds(List<Long> orderIds) {
        if (orderIds.isEmpty()) return List.of();

        String sql = "SELECT * FROM order_items WHERE order_id IN (" +
                getPlaceholders(orderIds.size()) + ")";
        return jdbcTemplate.query(sql, orderIds.toArray(), getRowMapper());
    }

    public void saveAll(List<OrderItem> items) {
        if (items.isEmpty()) return;

        String sql = """
            INSERT INTO order_items (order_id, product_id, quantity, product_title, product_url, price_cents, price_currency, created_at, updated_at) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        jdbcTemplate.batchUpdate(sql, items, items.size(), (ps, item) -> {
            ps.setLong(1, item.getOrder().getId());
            ps.setLong(2, item.getProductId());
            ps.setInt(3, item.getQuantity());
            ps.setString(4, item.getProductTitle());
            ps.setString(5, item.getProductUrl());
            ps.setLong(6, item.getPriceCents());
            ps.setString(7, item.getPriceCurrency());
            ps.setTimestamp(8, Timestamp.valueOf(item.getCreatedAt().toLocalDateTime()));
            ps.setTimestamp(9, Timestamp.valueOf(item.getUpdatedAt().toLocalDateTime()));
        });
    }

    private RowMapper<OrderItem> getRowMapper() {
        return (rs, rowNum) -> {
            OrderItem item = new OrderItem();
            item.setId(rs.getLong("id"));
            item.setProductId(rs.getLong("product_id"));
            item.setQuantity(rs.getInt("quantity"));
            item.setProductTitle(rs.getString("product_title"));
            item.setProductUrl(rs.getString("product_url"));
            item.setPriceCents(rs.getLong("price_cents"));
            item.setPriceCurrency(rs.getString("price_currency"));
            item.setCreatedAt(rs.getTimestamp("created_at").toInstant().atOffset(java.time.ZoneOffset.UTC));
            item.setUpdatedAt(rs.getTimestamp("updated_at").toInstant().atOffset(java.time.ZoneOffset.UTC));

            Order order = new Order();
            order.setId(rs.getLong("order_id"));
            item.setOrder(order);

            return item;
        };
    }

    private String getPlaceholders(int count) {
        return String.join(",", Collections.nCopies(count, "?"));
    }
}