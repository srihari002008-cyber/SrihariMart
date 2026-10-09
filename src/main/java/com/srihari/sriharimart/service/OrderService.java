package com.srihari.sriharimart.service;

import com.srihari.sriharimart.dao.CartDAO;
import com.srihari.sriharimart.dao.OrderDAO;
import com.srihari.sriharimart.dto.OrderRequestDTO;
import com.srihari.sriharimart.dto.OrderResponseDTO;
import com.srihari.sriharimart.exception.ResourceNotFoundException;
import com.srihari.sriharimart.exception.ValidationException;
import com.srihari.sriharimart.model.CartItem;
import com.srihari.sriharimart.model.Order;
import com.srihari.sriharimart.util.ValidationUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service managing checkout, order creation with mock payment, and order queries.
 */
public class OrderService {

    private final OrderDAO orderDAO;
    private final CartDAO cartDAO;

    public OrderService(OrderDAO orderDAO, CartDAO cartDAO) {
        this.orderDAO = orderDAO;
        this.cartDAO = cartDAO;
    }

    /**
     * Executes order placement from the buyer's active cart via mock payment confirmation.
     *
     * @param buyerId Buyer user ID
     * @param req     Checkout request containing address and payment method
     * @return Generated OrderResponseDTO
     */
    public OrderResponseDTO checkout(Long buyerId, OrderRequestDTO req) {
        if (buyerId == null) {
            throw new ValidationException("Buyer ID is required");
        }
        if (req == null || !ValidationUtil.isNotBlank(req.getShippingAddress())) {
            throw new ValidationException("Delivery address is required for checkout");
        }

        List<CartItem> cartItems = cartDAO.findByUserId(buyerId);
        if (cartItems.isEmpty()) {
            throw new ValidationException("Cannot place an order with an empty cart");
        }

        // Verify stock for all items prior to transaction
        for (CartItem item : cartItems) {
            if (item.getProduct() == null || item.getProduct().getStockQty() < item.getQuantity()) {
                String name = item.getProduct() != null ? item.getProduct().getName() : "Item " + item.getProductId();
                throw new ValidationException("Insufficient stock for product: " + name);
            }
        }

        BigDecimal total = cartItems.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order placedOrder = orderDAO.placeOrder(buyerId, cartItems, total);
        return OrderResponseDTO.fromEntity(placedOrder);
    }

    /**
     * Retrieves all orders placed by a buyer.
     *
     * @param buyerId Buyer user ID
     * @return List of OrderResponseDTO
     */
    public List<OrderResponseDTO> getBuyerOrders(Long buyerId) {
        if (buyerId == null) {
            throw new ValidationException("Buyer ID is required");
        }
        return orderDAO.findByBuyerId(buyerId).stream()
                .map(OrderResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves incoming orders containing items sold by a seller.
     *
     * @param sellerId Seller user ID
     * @return List of OrderResponseDTO
     */
    public List<OrderResponseDTO> getSellerOrders(Long sellerId) {
        if (sellerId == null) {
            throw new ValidationException("Seller ID is required");
        }
        return orderDAO.findBySellerId(sellerId).stream()
                .map(OrderResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a single order by ID.
     *
     * @param orderId Order ID
     * @return OrderResponseDTO
     * @throws ResourceNotFoundException if order does not exist
     */
    public OrderResponseDTO getOrderById(Long orderId) {
        if (orderId == null) {
            throw new ValidationException("Order ID is required");
        }
        Order order = orderDAO.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        return OrderResponseDTO.fromEntity(order);
    }
}
