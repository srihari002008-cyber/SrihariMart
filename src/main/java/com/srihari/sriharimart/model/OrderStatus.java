package com.srihari.sriharimart.model;

/**
 * Order fulfillment statuses.
 */
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED;

    public static OrderStatus fromString(String status) {
        if (status == null) {
            return null;
        }
        for (OrderStatus s : OrderStatus.values()) {
            if (s.name().equalsIgnoreCase(status.trim())) {
                return s;
            }
        }
        return null;
    }
}
