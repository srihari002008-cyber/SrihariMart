package com.srihari.sriharimart.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO representing the entire user cart with item list, count, and running total.
 */
public class CartResponseDTO {
    private List<CartItemDTO> items = new ArrayList<>();
    private int totalItems;
    private BigDecimal totalAmount = BigDecimal.ZERO;

    public CartResponseDTO() {
    }

    public CartResponseDTO(List<CartItemDTO> items, int totalItems, BigDecimal totalAmount) {
        this.items = items;
        this.totalItems = totalItems;
        this.totalAmount = totalAmount;
    }

    public List<CartItemDTO> getItems() {
        return items;
    }

    public void setItems(List<CartItemDTO> items) {
        this.items = items;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
