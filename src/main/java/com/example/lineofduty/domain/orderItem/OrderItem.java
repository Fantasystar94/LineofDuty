package com.example.lineofduty.domain.orderItem;

import com.example.lineofduty.domain.product.Product;
import com.example.lineofduty.entity.BaseEntity;
import com.example.lineofduty.domain.order.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "order_price", nullable = false)
    private Long orderPrice;

    @Column(nullable = false)
    private Long quantity;

    public OrderItem(Product product, Order order, Long orderPrice, Long quantity) {
        this.product = product;
        this.order = order;
        this.orderPrice = orderPrice;
        this.quantity = quantity;
    }

    public void updateProduct(Product product) {
        this.product = product;
    }

    public void updateQuantity(Long quantity) {
        this.quantity = quantity;
    }
}
