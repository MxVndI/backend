package ru.practicum.order_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.order_service.model.Order;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    @Query("SELECT o FROM Order o WHERE o.customerId IN :customerIds")
    Page<Order> findByCustomerIds(@Param("customerIds") List<Long> customerIds, Pageable pageable);

    Page<Order> findByIdIn(List<Long> ids, Pageable pageable);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.id IN :ids")
    List<Order> findByIdInWithItems(@Param("ids") List<Long> ids);
}
