package com.srihari.sriharimart.service;

import com.srihari.sriharimart.dao.CartDAO;
import com.srihari.sriharimart.dao.ProductDAO;
import com.srihari.sriharimart.dto.CartItemDTO;
import com.srihari.sriharimart.dto.CartResponseDTO;
import com.srihari.sriharimart.exception.ResourceNotFoundException;
import com.srihari.sriharimart.exception.ValidationException;
import com.srihari.sriharimart.model.CartItem;
import com.srihari.sriharimart.model.Product;
import com.srihari.sriharimart.util.ValidationUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service managing buyer shopping cart operations.
 */
public class CartService {

    private final CartDAO cartDAO;
    private final ProductDAO productDAO;

    public CartService(CartDAO cartDAO, ProductDAO productDAO) {
        this.cartDAO = cartDAO;
        this.productDAO = productDAO;
    }

    /**
     * Retrieves the complete cart for a buyer with running totals.
     *
     * @param userId Buyer user ID
     * @return CartResponseDTO containing items and running total
     */
    public CartResponseDTO getCart(Long userId) {
        if (userId == null) {
            throw new ValidationException("User ID is required");
        }

        List<CartItem> items = cartDAO.findByUserId(userId);
        List<CartItemDTO> dtos = items.stream()
                .map(CartItemDTO::fromEntity)
                .collect(Collectors.toList());

        BigDecimal total = items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalCount = items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        return new CartResponseDTO(dtos, totalCount, total);
    }

    /**
     * Adds a product to the user's cart with inventory check.
     *
     * @param userId    Buyer user ID
     * @param productId Product ID
     * @param quantity  Quantity to add
     */
    public void addToCart(Long userId, Long productId, int quantity) {
        if (userId == null || productId == null) {
            throw new ValidationException("User ID and Product ID are required");
        }
        if (!ValidationUtil.isPositiveQuantity(quantity)) {
            throw new ValidationException("Quantity must be greater than zero");
        }

        Product product = productDAO.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (product.getStockQty() < quantity) {
            throw new ValidationException("Insufficient stock available (" + product.getStockQty() + " in stock)");
        }

        cartDAO.addItem(userId, productId, quantity);
    }

    /**
     * Updates quantity of an item in cart. If quantity is 0 or negative, item is removed.
     *
     * @param userId     Buyer user ID
     * @param cartItemId Cart item ID
     * @param quantity   New quantity
     */
    public void updateQuantity(Long userId, Long cartItemId, int quantity) {
        if (userId == null || cartItemId == null) {
            throw new ValidationException("User ID and Cart Item ID are required");
        }

        if (quantity <= 0) {
            cartDAO.removeItem(userId, cartItemId);
            return;
        }

        cartDAO.updateQuantity(userId, cartItemId, quantity);
    }

    /**
     * Removes an item from the cart.
     *
     * @param userId     Buyer user ID
     * @param cartItemId Cart item ID
     */
    public void removeFromCart(Long userId, Long cartItemId) {
        if (userId == null || cartItemId == null) {
            throw new ValidationException("User ID and Cart Item ID are required");
        }
        cartDAO.removeItem(userId, cartItemId);
    }

    /**
     * Clears all items in the buyer's cart.
     *
     * @param userId Buyer user ID
     */
    public void clearCart(Long userId) {
        if (userId == null) {
            throw new ValidationException("User ID is required");
        }
        cartDAO.clearCart(userId);
    }
}
