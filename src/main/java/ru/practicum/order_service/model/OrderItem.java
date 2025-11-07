package ru.practicum.order_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Setter
@Getter
@Entity
@Table(name = "order_items")
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "product_title", nullable = false)
    private String productTitle;

    @Column(name = "product_url", nullable = false)
    private String productUrl;

    @Column(name = "price_cents", nullable = false)
    private Long priceCents;

    @Column(name = "price_currency", nullable = false, length = 3)
    private String priceCurrency;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public OrderItem(Long productId, Integer quantity, String productTitle,
                     String productUrl, Long priceCents, String priceCurrency) {
        this.productId = productId;
        this.quantity = quantity;
        this.productTitle = productTitle;
        this.productUrl = productUrl;
        this.priceCents = priceCents;
        this.priceCurrency = priceCurrency;
    }

}