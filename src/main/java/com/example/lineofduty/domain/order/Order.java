package com.example.lineofduty.domain.order;

import com.example.lineofduty.domain.orderItem.OrderItem;
import com.example.lineofduty.domain.user.User;
import com.example.lineofduty.entity.BaseEntity;
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

    @Column(name = "order_name", nullable = false)
    private String orderName;

    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber;

    @Column(name = "total_price", nullable = false)
    private Long totalPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItemList = new ArrayList<>();

    // (status == true) => 주문 완료(soft-delete), (status == false) => 주문 완료 안됨
    @Column(name = "is_order_completed", nullable = false)
    private boolean isOrderCompleted = false;

    public Order(User user, String orderName, String orderNumber, Long totalPrice, List<OrderItem> orderItemList) {
        this.user = user;
        this.orderName = orderName;
        this.orderNumber = orderNumber;
        this.totalPrice = totalPrice;
        this.orderItemList = orderItemList;
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItemList.add(orderItem);
        this.totalPrice += orderItem.getOrderPrice() * orderItem.getQuantity();
        orderItem.updateOrder(this);
    }

    public void updateTotalPrice(Long changedTotalPrice) {
        this.totalPrice = changedTotalPrice;
    }

    public void updateIsOrderCompleted(boolean status) {
        this.isOrderCompleted = status;
    }

    public void updateOrderName(String orderName) {
        this.orderName = orderName;
    }

}
