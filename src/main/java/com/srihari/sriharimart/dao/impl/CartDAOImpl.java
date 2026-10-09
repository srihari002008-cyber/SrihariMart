package com.srihari.sriharimart.dao.impl;

import com.srihari.sriharimart.dao.CartDAO;
import com.srihari.sriharimart.exception.AppException;
import com.srihari.sriharimart.model.CartItem;
import com.srihari.sriharimart.model.Product;
import com.srihari.sriharimart.util.DBConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of CartDAO utilizing PreparedStatement and connection pooling.
 */
public class CartDAOImpl implements CartDAO {

    private static final String SQL_FIND_BY_USER =
            "SELECT c.id, c.user_id, c.product_id, c.quantity, " +
                    "p.id AS p_id, p.seller_id, p.name, p.description, p.price, p.stock_qty, p.category, p.image_url, p.created_at " +
                    "FROM cart_items c " +
                    "JOIN products p ON c.product_id = p.id " +
                    "WHERE c.user_id = ? " +
                    "ORDER BY c.id ASC";

    private static final String SQL_FIND_BY_USER_AND_PRODUCT =
            "SELECT id, user_id, product_id, quantity FROM cart_items WHERE user_id = ? AND product_id = ?";

    private static final String SQL_INSERT =
            "INSERT INTO cart_items (user_id, product_id, quantity) VALUES (?, ?, ?)";

    private static final String SQL_UPDATE_QTY =
            "UPDATE cart_items SET quantity = ? WHERE id = ? AND user_id = ?";

    private static final String SQL_INCREMENT_QTY =
            "UPDATE cart_items SET quantity = quantity + ? WHERE id = ?";

    private static final String SQL_DELETE_ITEM =
            "DELETE FROM cart_items WHERE id = ? AND user_id = ?";

    private static final String SQL_CLEAR_CART =
            "DELETE FROM cart_items WHERE user_id = ?";

    /**
     * Retrieves all cart items for a specific buyer, populated with product details.
     *
     * @param userId Buyer user ID
     * @return List of CartItem entities
     */
    @Override
    public List<CartItem> findByUserId(Long userId) {
        List<CartItem> items = new ArrayList<>();
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_USER)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CartItem item = new CartItem();
                    item.setId(rs.getLong("id"));
                    item.setUserId(rs.getLong("user_id"));
                    item.setProductId(rs.getLong("product_id"));
                    item.setQuantity(rs.getInt("quantity"));

                    Product p = new Product();
                    p.setId(rs.getLong("p_id"));
                    p.setSellerId(rs.getLong("seller_id"));
                    p.setName(rs.getString("name"));
                    p.setDescription(rs.getString("description"));
                    p.setPrice(rs.getBigDecimal("price"));
                    p.setStockQty(rs.getInt("stock_qty"));
                    p.setCategory(rs.getString("category"));
                    p.setImageUrl(rs.getString("image_url"));
                    p.setCreatedAt(rs.getTimestamp("created_at"));

                    item.setProduct(p);
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            throw new AppException("Error fetching cart items for user: " + userId, e);
        }
        return items;
    }

    /**
     * Finds a single cart item for user and product.
     *
     * @param userId    User ID
     * @param productId Product ID
     * @return Optional containing CartItem if present
     */
    @Override
    public Optional<CartItem> findByUserAndProduct(Long userId, Long productId) {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_USER_AND_PRODUCT)) {
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CartItem item = new CartItem();
                    item.setId(rs.getLong("id"));
                    item.setUserId(rs.getLong("user_id"));
                    item.setProductId(rs.getLong("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    return Optional.of(item);
                }
            }
        } catch (SQLException e) {
            throw new AppException("Error querying cart item for user: " + userId + ", product: " + productId, e);
        }
        return Optional.empty();
    }

    /**
     * Adds an item to user cart, or increases quantity if already present.
     *
     * @param userId    Buyer user ID
     * @param productId Product ID
     * @param quantity  Quantity to add
     */
    @Override
    public void addItem(Long userId, Long productId, int quantity) {
        Optional<CartItem> existing = findByUserAndProduct(userId, productId);
        if (existing.isPresent()) {
            try (Connection conn = DBConnectionManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(SQL_INCREMENT_QTY)) {
                ps.setInt(1, quantity);
                ps.setLong(2, existing.get().getId());
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new AppException("Error incrementing cart item quantity", e);
            }
        } else {
            try (Connection conn = DBConnectionManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
                ps.setLong(1, userId);
                ps.setLong(2, productId);
                ps.setInt(3, quantity);
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new AppException("Error adding item to cart", e);
            }
        }
    }

    /**
     * Updates quantity of a specific cart item.
     *
     * @param userId     Buyer user ID
     * @param cartItemId Cart item ID
     * @param quantity   New quantity
     * @return true if updated
     */
    @Override
    public boolean updateQuantity(Long userId, Long cartItemId, int quantity) {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_QTY)) {
            ps.setInt(1, quantity);
            ps.setLong(2, cartItemId);
            ps.setLong(3, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AppException("Error updating cart item quantity", e);
        }
    }

    /**
     * Removes an item from the cart.
     *
     * @param userId     Buyer user ID
     * @param cartItemId Cart item ID
     * @return true if removed
     */
    @Override
    public boolean removeItem(Long userId, Long cartItemId) {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE_ITEM)) {
            ps.setLong(1, cartItemId);
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AppException("Error removing cart item", e);
        }
    }

    /**
     * Clears all items in a buyer's cart.
     *
     * @param userId Buyer user ID
     */
    @Override
    public void clearCart(Long userId) {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_CLEAR_CART)) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException("Error clearing cart for user: " + userId, e);
        }
    }

    /**
     * Clears all items in a buyer's cart within an active database transaction.
     *
     * @param conn   Open transactional JDBC Connection
     * @param userId Buyer user ID
     */
    @Override
    public void clearCart(Connection conn, Long userId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_CLEAR_CART)) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException("Error clearing cart in transaction for user: " + userId, e);
        }
    }
}
