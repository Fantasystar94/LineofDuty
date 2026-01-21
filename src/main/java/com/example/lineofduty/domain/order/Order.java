package com.example.lineofduty.domain.order;

import com.example.lineofduty.domain.orderItem.OrderItem;
import com.example.lineofduty.entity.BaseEntity;
import com.example.lineofduty.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "total_price", nullable = false)
    private Long totalPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    // (status == true) => 사용 중, (status == false) => 사용됨(soft-delete)
    @Column(name = "status", nullable = false)
    private boolean status = true;

    public Order(User user, Long totalPrice, List<OrderItem> orderItems) {
        this.user = user;
        this.totalPrice = totalPrice;
        this.orderItems = orderItems;
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        this.totalPrice += orderItem.getOrderPrice() * orderItem.getQuantity();
        orderItem.updateOrder(this);
    }

    public void updateTotalPrice(Long changedTotalPrice) {
        this.totalPrice = changedTotalPrice;
    }

    public void updateStatus(boolean status) {
        this.status = status;
    }

}
