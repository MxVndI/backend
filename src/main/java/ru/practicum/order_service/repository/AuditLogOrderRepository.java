package ru.practicum.order_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.order_service.model.AuditLogOrder;

public interface AuditLogOrderRepository extends JpaRepository<AuditLogOrder, Long> {
}
