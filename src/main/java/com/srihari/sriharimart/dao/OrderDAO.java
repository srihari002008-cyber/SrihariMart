package com.srihari.sriharimart.dao;

import com.srihari.sriharimart.model.CartItem;
import com.srihari.sriharimart.model.Order;
import com.srihari.sriharimart.model.OrderStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Order entities and line items.
 */
public interface OrderDAO {

    /**
     * Executes atomic placement of an order: creates order header, order items,
     * deducts product stock, and clears buyer cart within a single transaction.
     *
     * @param buyerId Buyer user ID
     * @param cartItems Active cart items
     * @param totalAmount Total calculated order amount
     * @return Generated and persisted Order with items
     */
    Order placeOrder(Long buyerId, List<CartItem> cartItems, BigDecimal totalAmount);

    /**
     * Finds an order by its ID with line items.
     *
     * @param orderId Order ID
     * @return Optional containing Order if found
     */
    Optional<Order> findById(Long orderId);

    /**
     * Retrieves all orders placed by a specific buyer.
     *
     * @param buyerId Buyer user ID
     * @return List of orders with items
     */
    List<Order> findByBuyerId(Long buyerId);

    /**
     * Retrieves orders containing items sold by a specific seller.
     *
     * @param sellerId Seller user ID
     * @return List of orders
     */
    List<Order> findBySellerId(Long sellerId);

    /**
     * Updates status of an order.
     *
     * @param orderId Order ID
     * @param status New order status
     * @return true if updated
     */
    boolean updateStatus(Long orderId, OrderStatus status);
}
