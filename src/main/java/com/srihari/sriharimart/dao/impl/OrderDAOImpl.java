package com.srihari.sriharimart.dao.impl;

import com.srihari.sriharimart.dao.OrderDAO;
import com.srihari.sriharimart.exception.AppException;
import com.srihari.sriharimart.exception.ValidationException;
import com.srihari.sriharimart.model.CartItem;
import com.srihari.sriharimart.model.Order;
import com.srihari.sriharimart.model.OrderItem;
import com.srihari.sriharimart.model.OrderStatus;
import com.srihari.sriharimart.util.DBConnectionManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of OrderDAO with ACID transaction handling.
 */
public class OrderDAOImpl implements OrderDAO {

    private static final String SQL_INSERT_ORDER =
            "INSERT INTO orders (buyer_id, status, total_amount, created_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

    private static final String SQL_INSERT_ORDER_ITEM =
            "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";

    private static final String SQL_DEDUCT_STOCK =
            "UPDATE products SET stock_qty = stock_qty - ? WHERE id = ? AND stock_qty >= ?";

    private static final String SQL_CLEAR_CART =
            "DELETE FROM cart_items WHERE user_id = ?";

    private static final String SQL_FIND_ORDER_BY_ID =
            "SELECT id, buyer_id, status, total_amount, created_at FROM orders WHERE id = ?";

    private static final String SQL_FIND_ITEMS_BY_ORDER_ID =
            "SELECT oi.id, oi.order_id, oi.product_id, oi.quantity, oi.unit_price, p.name AS product_name, p.image_url AS product_image_url " +
                    "FROM order_items oi " +
                    "JOIN products p ON oi.product_id = p.id " +
                    "WHERE oi.order_id = ? " +
                    "ORDER BY oi.id ASC";

    private static final String SQL_FIND_BY_BUYER =
            "SELECT id, buyer_id, status, total_amount, created_at FROM orders WHERE buyer_id = ? ORDER BY id DESC";

    private static final String SQL_FIND_BY_SELLER =
            "SELECT DISTINCT o.id, o.buyer_id, o.status, o.total_amount, o.created_at " +
                    "FROM orders o " +
                    "JOIN order_items oi ON o.id = oi.order_id " +
                    "JOIN products p ON oi.product_id = p.id " +
                    "WHERE p.seller_id = ? " +
                    "ORDER BY o.id DESC";

    private static final String SQL_UPDATE_STATUS =
            "UPDATE orders SET status = ? WHERE id = ?";

    /**
     * Executes atomic placement of an order: creates order header, order items,
     * deducts product stock, and clears buyer cart within a single transaction.
     *
     * @param buyerId     Buyer user ID
     * @param cartItems   Active cart items
     * @param totalAmount Total calculated order amount
     * @return Generated and persisted Order with items
     */
    @Override
    public Order placeOrder(Long buyerId, List<CartItem> cartItems, BigDecimal totalAmount) {
        Connection conn = null;
        try {
            conn = DBConnectionManager.getConnection();
            conn.setAutoCommit(false);

            // 1. Insert Order header
            Long orderId;
            try (PreparedStatement psOrder = conn.prepareStatement(SQL_INSERT_ORDER, Statement.RETURN_GENERATED_KEYS)) {
                psOrder.setLong(1, buyerId);
                psOrder.setString(2, OrderStatus.CONFIRMED.name());
                psOrder.setBigDecimal(3, totalAmount);
                psOrder.executeUpdate();

                try (ResultSet rs = psOrder.getGeneratedKeys()) {
                    if (!rs.next()) {
                        throw new SQLException("Failed to generate order primary key");
                    }
                    orderId = rs.getLong(1);
                }
            }

            // 2. Insert Order items and deduct stock
            List<OrderItem> createdItems = new ArrayList<>();
            for (CartItem item : cartItems) {
                // Deduct stock
                try (PreparedStatement psStock = conn.prepareStatement(SQL_DEDUCT_STOCK)) {
                    psStock.setInt(1, item.getQuantity());
                    psStock.setLong(2, item.getProductId());
                    psStock.setInt(3, item.getQuantity());
                    int updated = psStock.executeUpdate();
                    if (updated == 0) {
                        throw new ValidationException("Product " + item.getProduct().getName() + " does not have sufficient stock");
                    }
                }

                // Insert line item
                try (PreparedStatement psItem = conn.prepareStatement(SQL_INSERT_ORDER_ITEM, Statement.RETURN_GENERATED_KEYS)) {
                    psItem.setLong(1, orderId);
                    psItem.setLong(2, item.getProductId());
                    psItem.setInt(3, item.getQuantity());
                    psItem.setBigDecimal(4, item.getProduct().getPrice());
                    psItem.executeUpdate();

                    OrderItem orderItem = new OrderItem(
                            null,
                            orderId,
                            item.getProductId(),
                            item.getQuantity(),
                            item.getProduct().getPrice()
                    );
                    orderItem.setProductName(item.getProduct().getName());
                    orderItem.setProductImageUrl(item.getProduct().getImageUrl());

                    try (ResultSet rsItem = psItem.getGeneratedKeys()) {
                        if (rsItem.next()) {
                            orderItem.setId(rsItem.getLong(1));
                        }
                    }
                    createdItems.add(orderItem);
                }
            }

            // 3. Clear buyer cart
            try (PreparedStatement psClear = conn.prepareStatement(SQL_CLEAR_CART)) {
                psClear.setLong(1, buyerId);
                psClear.executeUpdate();
            }

            conn.commit();

            Order order = new Order();
            order.setId(orderId);
            order.setBuyerId(buyerId);
            order.setStatus(OrderStatus.CONFIRMED);
            order.setTotalAmount(totalAmount);
            order.setItems(createdItems);
            return order;

        } catch (SQLException | RuntimeException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    // Suppress rollback error
                }
            }
            if (e instanceof ValidationException ve) {
                throw ve;
            }
            throw new AppException("Failed to place order transaction", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    // Suppress close error
                }
            }
        }
    }

    /**
     * Finds an order by its ID with line items.
     *
     * @param orderId Order ID
     * @return Optional containing Order if found
     */
    @Override
    public Optional<Order> findById(Long orderId) {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ORDER_BY_ID)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order order = mapResultSetToOrder(rs);
                    order.setItems(findItemsForOrder(conn, orderId));
                    return Optional.of(order);
                }
            }
        } catch (SQLException e) {
            throw new AppException("Error finding order by ID: " + orderId, e);
        }
        return Optional.empty();
    }

    /**
     * Retrieves all orders placed by a specific buyer.
     *
     * @param buyerId Buyer user ID
     * @return List of orders with items
     */
    @Override
    public List<Order> findByBuyerId(Long buyerId) {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_BUYER)) {
            ps.setLong(1, buyerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = mapResultSetToOrder(rs);
                    order.setItems(findItemsForOrder(conn, order.getId()));
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            throw new AppException("Error finding orders for buyer: " + buyerId, e);
        }
        return orders;
    }

    /**
     * Retrieves orders containing items sold by a specific seller.
     *
     * @param sellerId Seller user ID
     * @return List of orders
     */
    @Override
    public List<Order> findBySellerId(Long sellerId) {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_SELLER)) {
            ps.setLong(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = mapResultSetToOrder(rs);
                    order.setItems(findItemsForOrder(conn, order.getId()));
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            throw new AppException("Error finding orders for seller: " + sellerId, e);
        }
        return orders;
    }

    /**
     * Updates status of an order.
     *
     * @param orderId Order ID
     * @param status  New order status
     * @return true if updated
     */
    @Override
    public boolean updateStatus(Long orderId, OrderStatus status) {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_STATUS)) {
            ps.setString(1, status.name());
            ps.setLong(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AppException("Error updating order status for id: " + orderId, e);
        }
    }

    private List<OrderItem> findItemsForOrder(Connection conn, Long orderId) throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ITEMS_BY_ORDER_ID)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setId(rs.getLong("id"));
                    item.setOrderId(rs.getLong("order_id"));
                    item.setProductId(rs.getLong("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setUnitPrice(rs.getBigDecimal("unit_price"));
                    item.setProductName(rs.getString("product_name"));
                    item.setProductImageUrl(rs.getString("product_image_url"));
                    items.add(item);
                }
            }
        }
        return items;
    }

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getLong("id"));
        o.setBuyerId(rs.getLong("buyer_id"));
        o.setStatus(OrderStatus.fromString(rs.getString("status")));
        o.setTotalAmount(rs.getBigDecimal("total_amount"));
        o.setCreatedAt(rs.getTimestamp("created_at"));
        return o;
    }
}
