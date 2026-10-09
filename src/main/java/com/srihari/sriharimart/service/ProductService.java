package com.srihari.sriharimart.service;

import com.srihari.sriharimart.dao.ProductDAO;
import com.srihari.sriharimart.dto.ProductDTO;
import com.srihari.sriharimart.exception.AuthorizationException;
import com.srihari.sriharimart.exception.ResourceNotFoundException;
import com.srihari.sriharimart.exception.ValidationException;
import com.srihari.sriharimart.model.Product;
import com.srihari.sriharimart.util.ValidationUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service managing product catalog queries and seller listing operations.
 * Enforces business logic and validation per Section 12 & 13.
 */
public class ProductService {

    private final ProductDAO productDAO;

    public ProductService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    /**
     * Retrieves all available products in the catalog.
     *
     * @return List of ProductDTO
     */
    public List<ProductDTO> getAllProducts() {
        return productDAO.findAll().stream()
                .map(ProductDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Searches and filters products by optional category and keyword.
     *
     * @param category Category filter (optional)
     * @param keyword  Search keyword (optional)
     * @return Filtered list of ProductDTO
     */
    public List<ProductDTO> searchProducts(String category, String keyword) {
        return productDAO.findByCategoryAndKeyword(category, keyword).stream()
                .map(ProductDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves details for a specific product by ID.
     *
     * @param id Product ID
     * @return ProductDTO
     * @throws ResourceNotFoundException if product does not exist
     */
    public ProductDTO getProductById(Long id) {
        if (id == null) {
            throw new ValidationException("Product ID is required");
        }
        Product product = productDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return ProductDTO.fromEntity(product);
    }

    /**
     * Retrieves all product listings managed by a specific seller.
     *
     * @param sellerId Seller user ID
     * @return List of ProductDTO
     */
    public List<ProductDTO> getProductsBySeller(Long sellerId) {
        if (sellerId == null) {
            throw new ValidationException("Seller ID is required");
        }
        return productDAO.findBySellerId(sellerId).stream()
                .map(ProductDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves distinct product categories available in the store.
     *
     * @return List of category names
     */
    public List<String> getAllCategories() {
        return productDAO.findAllCategories();
    }

    /**
     * Creates a new product listing for a seller after full validation.
     *
     * @param sellerId Seller user ID
     * @param dto      Product details
     * @return Persisted ProductDTO
     * @throws ValidationException if validation fails
     */
    public ProductDTO createProduct(Long sellerId, ProductDTO dto) {
        if (sellerId == null) {
            throw new ValidationException("Seller ID is required");
        }
        validateProduct(dto);

        Product product = new Product();
        product.setSellerId(sellerId);
        product.setName(dto.getName().trim());
        product.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : "");
        product.setPrice(dto.getPrice());
        product.setStockQty(dto.getStockQty());
        product.setCategory(dto.getCategory().trim());
        product.setImageUrl(dto.getImageUrl() != null ? dto.getImageUrl().trim() : "");

        Product saved = productDAO.save(product);
        return ProductDTO.fromEntity(saved);
    }

    /**
     * Updates an existing product listing owned by the seller.
     *
     * @param sellerId Seller user ID
     * @param dto      Updated product details
     * @return Updated ProductDTO
     * @throws ResourceNotFoundException if product not found
     * @throws AuthorizationException    if seller does not own this product
     * @throws ValidationException       if validation fails
     */
    public ProductDTO updateProduct(Long sellerId, ProductDTO dto) {
        if (sellerId == null) {
            throw new ValidationException("Seller ID is required");
        }
        if (dto == null || dto.getId() == null) {
            throw new ValidationException("Product ID is required for update");
        }

        Product existing = productDAO.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + dto.getId()));

        if (!existing.getSellerId().equals(sellerId)) {
            throw new AuthorizationException("You are not authorized to update this product");
        }

        validateProduct(dto);

        existing.setName(dto.getName().trim());
        existing.setDescription(dto.getDescription() != null ? dto.getDescription().trim() : "");
        existing.setPrice(dto.getPrice());
        existing.setStockQty(dto.getStockQty());
        existing.setCategory(dto.getCategory().trim());
        existing.setImageUrl(dto.getImageUrl() != null ? dto.getImageUrl().trim() : "");

        productDAO.update(existing);
        return ProductDTO.fromEntity(existing);
    }

    /**
     * Deletes a product listing owned by the seller.
     *
     * @param sellerId  Seller user ID
     * @param productId Product ID to delete
     * @throws ResourceNotFoundException if product not found
     * @throws AuthorizationException    if seller does not own product
     */
    public void deleteProduct(Long sellerId, Long productId) {
        if (sellerId == null || productId == null) {
            throw new ValidationException("Seller ID and Product ID are required");
        }

        Product existing = productDAO.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (!existing.getSellerId().equals(sellerId)) {
            throw new AuthorizationException("You are not authorized to delete this product");
        }

        productDAO.deleteByIdAndSellerId(productId, sellerId);
    }

    private void validateProduct(ProductDTO dto) {
        Map<String, String> errors = new HashMap<>();

        if (dto == null) {
            throw new ValidationException("Product data cannot be null");
        }

        if (!ValidationUtil.isNotBlank(dto.getName())) {
            errors.put("name", "Product name is required");
        }

        if (!ValidationUtil.isPositiveAmount(dto.getPrice())) {
            errors.put("price", "Price must be a positive decimal number");
        }

        if (!ValidationUtil.isNonNegativeQuantity(dto.getStockQty())) {
            errors.put("stockQty", "Stock quantity cannot be negative");
        }

        if (!ValidationUtil.isNotBlank(dto.getCategory())) {
            errors.put("category", "Product category is required");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Product validation failed", errors);
        }
    }
}
