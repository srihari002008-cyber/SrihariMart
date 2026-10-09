package com.srihari.sriharimart.dao;

import com.srihari.sriharimart.model.Product;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Product entities.
 */
public interface ProductDAO {

    /**
     * Finds a product by ID.
     *
     * @param id Product ID
     * @return Optional containing Product if found
     */
    Optional<Product> findById(Long id);

    /**
     * Retrieves all products ordered by creation date descending.
     *
     * @return List of products
     */
    List<Product> findAll();

    /**
     * Searches and filters products by category and keyword.
     *
     * @param category Category name (optional)
     * @param keyword Search keyword (optional)
     * @return Filtered list of products
     */
    List<Product> findByCategoryAndKeyword(String category, String keyword);

    /**
     * Retrieves products listed by a specific seller.
     *
     * @param sellerId Seller user ID
     * @return List of products for the seller
     */
    List<Product> findBySellerId(Long sellerId);

    /**
     * Retrieves list of distinct categories currently in catalog.
     *
     * @return List of category names
     */
    List<String> findAllCategories();

    /**
     * Persists a new product listing.
     *
     * @param product Product to persist
     * @return Persisted product with generated ID
     */
    Product save(Product product);

    /**
     * Updates an existing product listing.
     *
     * @param product Product with updated fields
     * @return true if updated, false otherwise
     */
    boolean update(Product product);

    /**
     * Deletes a product listing belonging to a seller.
     *
     * @param id Product ID
     * @param sellerId Seller user ID
     * @return true if deleted, false otherwise
     */
    boolean deleteByIdAndSellerId(Long id, Long sellerId);

    /**
     * Decreases product stock during order placement within a transaction.
     *
     * @param conn Open transactional JDBC Connection
     * @param productId Product ID
     * @param quantity Quantity to deduct
     * @return true if stock was successfully decremented, false if insufficient stock
     */
    boolean deductStock(Connection conn, Long productId, int quantity);
}
