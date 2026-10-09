package com.srihari.sriharimart.dto;

import com.srihari.sriharimart.model.Order;
import com.srihari.sriharimart.model.OrderItem;
import com.srihari.sriharimart.model.OrderStatus;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO representing an Order with its items for responses.
 */
public class OrderResponseDTO {
    private Long id;
    private Long buyerId;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private Timestamp createdAt;
    private List<OrderItem> items = new ArrayList<>();

    public OrderResponseDTO() {
    }

    public static OrderResponseDTO fromEntity(Order order) {
        if (order == null) {
            return null;
        }
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setBuyerId(order.getBuyerId());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setItems(order.getItems());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}
