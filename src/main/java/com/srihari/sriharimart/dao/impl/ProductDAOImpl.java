package com.srihari.sriharimart.dao.impl;

import com.srihari.sriharimart.dao.ProductDAO;
import com.srihari.sriharimart.exception.AppException;
import com.srihari.sriharimart.model.Product;
import com.srihari.sriharimart.util.DBConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of ProductDAO utilizing PreparedStatement and connection pooling.
 */
public class ProductDAOImpl implements ProductDAO {

    private static final String SQL_FIND_BY_ID =
            "SELECT id, seller_id, name, description, price, stock_qty, category, image_url, created_at FROM products WHERE id = ?";

    private static final String SQL_FIND_ALL =
            "SELECT id, seller_id, name, description, price, stock_qty, category, image_url, created_at FROM products ORDER BY id DESC";

    private static final String SQL_FIND_BY_SELLER =
            "SELECT id, seller_id, name, description, price, stock_qty, category, image_url, created_at FROM products WHERE seller_id = ? ORDER BY id DESC";

    private static final String SQL_FIND_CATEGORIES =
            "SELECT DISTINCT category FROM products WHERE category IS NOT NULL ORDER BY category ASC";

    private static final String SQL_SEARCH =
            "SELECT id, seller_id, name, description, price, stock_qty, category, image_url, created_at FROM products " +
                    "WHERE (? IS NULL OR category = ?) " +
                    "AND (? IS NULL OR LOWER(name) LIKE ? OR LOWER(description) LIKE ?) " +
                    "ORDER BY id DESC";

    private static final String SQL_INSERT =
            "INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

    private static final String SQL_UPDATE =
            "UPDATE products SET name = ?, description = ?, price = ?, stock_qty = ?, category = ?, image_url = ? " +
                    "WHERE id = ? AND seller_id = ?";

    private static final String SQL_DELETE =
            "DELETE FROM products WHERE id = ? AND seller_id = ?";

    private static final String SQL_DEDUCT_STOCK =
            "UPDATE products SET stock_qty = stock_qty - ? WHERE id = ? AND stock_qty >= ?";

    /**
     * Finds a product by ID.
     *
     * @param id Product ID
     * @return Optional containing Product if found
     */
    @Override
    public Optional<Product> findById(Long id) {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            throw new AppException("Error finding product by id: " + id, e);
        }
        return Optional.empty();
    }

    /**
     * Retrieves all products ordered by creation date descending.
     *
     * @return List of products
     */
    @Override
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            throw new AppException("Error fetching all products", e);
        }
        return products;
    }

    /**
     * Searches and filters products by category and keyword.
     *
     * @param category Category name (optional)
     * @param keyword  Search keyword (optional)
     * @return Filtered list of products
     */
    @Override
    public List<Product> findByCategoryAndKeyword(String category, String keyword) {
        String cleanCategory = (category != null && !category.trim().isEmpty()) ? category.trim() : null;
        String pattern = null;
        if (keyword != null && !keyword.trim().isEmpty()) {
            pattern = "%" + keyword.trim().toLowerCase() + "%";
        }

        List<Product> products = new ArrayList<>();
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SEARCH)) {
            ps.setString(1, cleanCategory);
            ps.setString(2, cleanCategory);
            ps.setString(3, pattern);
            ps.setString(4, pattern);
            ps.setString(5, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            throw new AppException("Error searching products", e);
        }
        return products;
    }

    /**
     * Retrieves products listed by a specific seller.
     *
     * @param sellerId Seller user ID
     * @return List of products for the seller
     */
    @Override
    public List<Product> findBySellerId(Long sellerId) {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_SELLER)) {
            ps.setLong(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            throw new AppException("Error fetching products for seller: " + sellerId, e);
        }
        return products;
    }

    /**
     * Retrieves list of distinct categories currently in catalog.
     *
     * @return List of category names
     */
    @Override
    public List<String> findAllCategories() {
        List<String> categories = new ArrayList<>();
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_CATEGORIES);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categories.add(rs.getString(1));
            }
        } catch (SQLException e) {
            throw new AppException("Error fetching product categories", e);
        }
        return categories;
    }

    /**
     * Persists a new product listing.
     *
     * @param product Product to persist
     * @return Persisted product with generated ID
     */
    @Override
    public Product save(Product product) {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, product.getSellerId());
            ps.setString(2, product.getName());
            ps.setString(3, product.getDescription());
            ps.setBigDecimal(4, product.getPrice());
            ps.setInt(5, product.getStockQty());
            ps.setString(6, product.getCategory());
            ps.setString(7, product.getImageUrl());

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    product.setId(generatedKeys.getLong(1));
                }
            }
            return product;
        } catch (SQLException e) {
            throw new AppException("Error saving product: " + product.getName(), e);
        }
    }

    /**
     * Updates an existing product listing.
     *
     * @param product Product with updated fields
     * @return true if updated, false otherwise
     */
    @Override
    public boolean update(Product product) {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setBigDecimal(3, product.getPrice());
            ps.setInt(4, product.getStockQty());
            ps.setString(5, product.getCategory());
            ps.setString(6, product.getImageUrl());
            ps.setLong(7, product.getId());
            ps.setLong(8, product.getSellerId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AppException("Error updating product: " + product.getId(), e);
        }
    }

    /**
     * Deletes a product listing belonging to a seller.
     *
     * @param id       Product ID
     * @param sellerId Seller user ID
     * @return true if deleted, false otherwise
     */
    @Override
    public boolean deleteByIdAndSellerId(Long id, Long sellerId) {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setLong(1, id);
            ps.setLong(2, sellerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AppException("Error deleting product: " + id, e);
        }
    }

    /**
     * Decreases product stock during order placement within a transaction.
     *
     * @param conn      Open transactional JDBC Connection
     * @param productId Product ID
     * @param quantity  Quantity to deduct
     * @return true if stock was successfully decremented, false if insufficient stock
     */
    @Override
    public boolean deductStock(Connection conn, Long productId, int quantity) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_DEDUCT_STOCK)) {
            ps.setInt(1, quantity);
            ps.setLong(2, productId);
            ps.setInt(3, quantity);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            throw new AppException("Error deducting stock for product: " + productId, e);
        }
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getLong("id"));
        p.setSellerId(rs.getLong("seller_id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setStockQty(rs.getInt("stock_qty"));
        p.setCategory(rs.getString("category"));
        p.setImageUrl(rs.getString("image_url"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        return p;
    }
}
