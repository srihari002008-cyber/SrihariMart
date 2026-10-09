package com.srihari.sriharimart.dao;

import com.srihari.sriharimart.model.CartItem;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Cart items.
 */
public interface CartDAO {

    /**
     * Retrieves all cart items for a specific buyer, populated with product details.
     *
     * @param userId Buyer user ID
     * @return List of CartItem entities
     */
    List<CartItem> findByUserId(Long userId);

    /**
     * Finds a single cart item for user and product.
     *
     * @param userId User ID
     * @param productId Product ID
     * @return Optional containing CartItem if present
     */
    Optional<CartItem> findByUserAndProduct(Long userId, Long productId);

    /**
     * Adds an item to user cart, or increases quantity if already present.
     *
     * @param userId Buyer user ID
     * @param productId Product ID
     * @param quantity Quantity to add
     */
    void addItem(Long userId, Long productId, int quantity);

    /**
     * Updates quantity of a specific cart item.
     *
     * @param userId Buyer user ID
     * @param cartItemId Cart item ID
     * @param quantity New quantity
     * @return true if updated
     */
    boolean updateQuantity(Long userId, Long cartItemId, int quantity);

    /**
     * Removes an item from the cart.
     *
     * @param userId Buyer user ID
     * @param cartItemId Cart item ID
     * @return true if removed
     */
    boolean removeItem(Long userId, Long cartItemId);

    /**
     * Clears all items in a buyer's cart.
     *
     * @param userId Buyer user ID
     */
    void clearCart(Long userId);

    /**
     * Clears all items in a buyer's cart within an active database transaction.
     *
     * @param conn Open transactional JDBC Connection
     * @param userId Buyer user ID
     */
    void clearCart(Connection conn, Long userId);
}
