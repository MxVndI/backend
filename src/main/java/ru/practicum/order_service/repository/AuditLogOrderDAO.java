package ru.practicum.order_service.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.practicum.order_service.model.AuditLogOrder;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class AuditLogOrderDAO {
    private final JdbcTemplate jdbcTemplate;

    public AuditLogOrderDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<AuditLogOrder> findById(Long id) {
        String sql = "SELECT * FROM audit_log_order WHERE id = ?";
        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, getRowMapper(), id));
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<AuditLogOrder> findAll() {
        String sql = "SELECT * FROM audit_log_order ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, getRowMapper());
    }

    public void save(AuditLogOrder auditLogOrder) {
        String sql = """
            INSERT INTO audit_log_order (order_id, order_item_id, customer_id, order_status, created_at, updated_at) 
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        jdbcTemplate.update(sql,
                auditLogOrder.getOrderId(),
                auditLogOrder.getOrderItemId(),
                auditLogOrder.getCustomerId(),
                auditLogOrder.getOrderStatus(),
                Timestamp.valueOf(auditLogOrder.getCreatedAt().toLocalDateTime()),
                Timestamp.valueOf(auditLogOrder.getUpdatedAt().toLocalDateTime()));
    }

    public void saveAll(List<AuditLogOrder> logs) {
        if (logs.isEmpty()) return;

        String sql = """
            INSERT INTO audit_log_order (order_id, order_item_id, customer_id, order_status, created_at, updated_at) 
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        jdbcTemplate.batchUpdate(sql, logs, logs.size(), (ps, log) -> {
            ps.setLong(1, log.getOrderId());
            ps.setLong(2, log.getOrderItemId());
            ps.setLong(3, log.getCustomerId());
            ps.setString(4, log.getOrderStatus());
            ps.setTimestamp(5, Timestamp.valueOf(log.getCreatedAt().toLocalDateTime()));
            ps.setTimestamp(6, Timestamp.valueOf(log.getUpdatedAt().toLocalDateTime()));
        });
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM audit_log_order WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM audit_log_order WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    private RowMapper<AuditLogOrder> getRowMapper() {
        return (rs, rowNum) -> AuditLogOrder.builder()
                .id(rs.getLong("id"))
                .orderId(rs.getLong("order_id"))
                .orderItemId(rs.getLong("order_item_id"))
                .customerId(rs.getLong("customer_id"))
                .orderStatus(rs.getString("order_status"))
                .createdAt(rs.getTimestamp("created_at").toInstant().atOffset(java.time.ZoneOffset.UTC))
                .updatedAt(rs.getTimestamp("updated_at").toInstant().atOffset(java.time.ZoneOffset.UTC))
                .build();
    }
}