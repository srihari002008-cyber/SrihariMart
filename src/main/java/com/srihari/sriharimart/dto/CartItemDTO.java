package com.srihari.sriharimart.dto;

import com.srihari.sriharimart.model.CartItem;

import java.math.BigDecimal;

/**
 * DTO representing an item in the cart.
 */
public class CartItemDTO {
    private Long id;
    private Long productId;
    private String productName;
    private String productCategory;
    private String productImageUrl;
    private BigDecimal price;
    private int quantity;
    private int stockQty;
    private BigDecimal subtotal;

    public CartItemDTO() {
    }

    public static CartItemDTO fromEntity(CartItem item) {
        if (item == null) {
            return null;
        }
        CartItemDTO dto = new CartItemDTO();
        dto.setId(item.getId());
        dto.setProductId(item.getProductId());
        dto.setQuantity(item.getQuantity());

        if (item.getProduct() != null) {
            dto.setProductName(item.getProduct().getName());
            dto.setProductCategory(item.getProduct().getCategory());
            dto.setProductImageUrl(item.getProduct().getImageUrl());
            dto.setPrice(item.getProduct().getPrice());
            dto.setStockQty(item.getProduct().getStockQty());
            dto.setSubtotal(item.getSubtotal());
        }
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getStockQty() {
        return stockQty;
    }

    public void setStockQty(int stockQty) {
        this.stockQty = stockQty;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
