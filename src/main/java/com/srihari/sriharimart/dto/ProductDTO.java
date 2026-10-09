package com.srihari.sriharimart.dto;

import com.srihari.sriharimart.model.Product;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Data Transfer Object for Product with Builder pattern support.
 */
public class ProductDTO {
    private Long id;
    private Long sellerId;
    private String name;
    private String description;
    private BigDecimal price;
    private int stockQty;
    private String category;
    private String imageUrl;
    private Timestamp createdAt;

    public ProductDTO() {
    }

    private ProductDTO(Builder builder) {
        this.id = builder.id;
        this.sellerId = builder.sellerId;
        this.name = builder.name;
        this.description = builder.description;
        this.price = builder.price;
        this.stockQty = builder.stockQty;
        this.category = builder.category;
        this.imageUrl = builder.imageUrl;
        this.createdAt = builder.createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static ProductDTO fromEntity(Product product) {
        if (product == null) {
            return null;
        }
        return builder()
                .id(product.getId())
                .sellerId(product.getSellerId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQty(product.getStockQty())
                .category(product.getCategory())
                .imageUrl(product.getImageUrl())
                .createdAt(product.getCreatedAt())
                .build();
    }

    public Product toEntity() {
        return new Product(id, sellerId, name, description, price, stockQty, category, imageUrl, createdAt);
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

    public static class Builder {
        private Long id;
        private Long sellerId;
        private String name;
        private String description;
        private BigDecimal price;
        private int stockQty;
        private String category;
        private String imageUrl;
        private Timestamp createdAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder sellerId(Long sellerId) {
            this.sellerId = sellerId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder stockQty(int stockQty) {
            this.stockQty = stockQty;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder createdAt(Timestamp createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ProductDTO build() {
            return new ProductDTO(this);
        }
    }
}
