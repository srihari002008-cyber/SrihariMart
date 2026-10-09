package com.srihari.sriharimart.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Entity representing a Product listed in SrihariMart.
 */
public class Product {
    private Long id;
    private Long sellerId;
    private String name;
    private String description;
    private BigDecimal price;
    private int stockQty;
    private String category;
    private String imageUrl;
    private Timestamp createdAt;

    public Product() {
    }

    public Product(Long id, Long sellerId, String name, String description, BigDecimal price,
                   int stockQty, String category, String imageUrl, Timestamp createdAt) {
        this.id = id;
        this.sellerId = sellerId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQty = stockQty;
        this.category = category;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStockQty() {
        return stockQty;
    }

    public void setStockQty(int stockQty) {
        this.stockQty = stockQty;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
