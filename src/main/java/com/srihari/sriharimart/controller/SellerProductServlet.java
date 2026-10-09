package com.srihari.sriharimart.controller;

import com.srihari.sriharimart.dao.DAOFactory;
import com.srihari.sriharimart.dto.ApiResponse;
import com.srihari.sriharimart.dto.ProductDTO;
import com.srihari.sriharimart.exception.AuthorizationException;
import com.srihari.sriharimart.exception.ResourceNotFoundException;
import com.srihari.sriharimart.exception.ValidationException;
import com.srihari.sriharimart.filter.AuthFilter;
import com.srihari.sriharimart.model.Role;
import com.srihari.sriharimart.model.User;
import com.srihari.sriharimart.service.ProductService;
import com.srihari.sriharimart.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Controller providing Seller Dashboard and listing management (create, edit, delete).
 * Delivers Week 3 and F2 requirements.
 */
@WebServlet(name = "SellerProductServlet", urlPatterns = {
        "/seller/dashboard",
        "/seller/products/create",
        "/seller/products/edit",
        "/seller/products/delete",
        "/api/v1/seller/products",
        "/api/v1/seller/products/*"
})
public class SellerProductServlet extends HttpServlet {

    private final ProductService productService = new ProductService(DAOFactory.getProductDAO());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User seller = getAuthenticatedSeller(req, resp);
        if (seller == null) {
            return;
        }

        String servletPath = req.getServletPath();
        String pathInfo = req.getPathInfo();

        if ("/seller/dashboard".equals(servletPath)) {
            List<ProductDTO> products = productService.getProductsBySeller(seller.getId());
            List<String> categories = productService.getAllCategories();

            req.setAttribute("products", products);
            req.setAttribute("categories", categories);
            req.getRequestDispatcher("/WEB-INF/views/seller/dashboard.jsp").forward(req, resp);

        } else if ("/api/v1/seller/products".equals(servletPath) && (pathInfo == null || "/".equals(pathInfo))) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            List<ProductDTO> products = productService.getProductsBySeller(seller.getId());
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.success(products)));
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User seller = getAuthenticatedSeller(req, resp);
        if (seller == null) {
            return;
        }

        String servletPath = req.getServletPath();

        try {
            if ("/seller/products/create".equals(servletPath)) {
                handleCreate(req, resp, seller);
            } else if ("/seller/products/edit".equals(servletPath)) {
                handleEdit(req, resp, seller);
            } else if ("/seller/products/delete".equals(servletPath)) {
                handleDelete(req, resp, seller);
            } else if ("/api/v1/seller/products".equals(servletPath)) {
                handleApiCreate(req, resp, seller);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (ValidationException e) {
            req.getSession().setAttribute("sellerError", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/seller/dashboard");
        } catch (AuthorizationException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        }
    }

    private void handleCreate(HttpServletRequest req, HttpServletResponse resp, User seller)
            throws IOException {
        String name = req.getParameter("name");
        String description = req.getParameter("description");
        String priceStr = req.getParameter("price");
        String stockStr = req.getParameter("stockQty");
        String category = req.getParameter("category");
        String imageUrl = req.getParameter("imageUrl");

        BigDecimal price = new BigDecimal(priceStr != null && !priceStr.trim().isEmpty() ? priceStr.trim() : "0");
        int stockQty = Integer.parseInt(stockStr != null && !stockStr.trim().isEmpty() ? stockStr.trim() : "0");

        ProductDTO dto = ProductDTO.builder()
                .sellerId(seller.getId())
                .name(name)
                .description(description)
                .price(price)
                .stockQty(stockQty)
                .category(category)
                .imageUrl(imageUrl)
                .build();

        productService.createProduct(seller.getId(), dto);
        req.getSession().setAttribute("sellerSuccess", "Product listing created successfully!");
        resp.sendRedirect(req.getContextPath() + "/seller/dashboard");
    }

    private void handleEdit(HttpServletRequest req, HttpServletResponse resp, User seller)
            throws IOException {
        Long id = Long.parseLong(req.getParameter("id"));
        String name = req.getParameter("name");
        String description = req.getParameter("description");
        String priceStr = req.getParameter("price");
        String stockStr = req.getParameter("stockQty");
        String category = req.getParameter("category");
        String imageUrl = req.getParameter("imageUrl");

        BigDecimal price = new BigDecimal(priceStr != null && !priceStr.trim().isEmpty() ? priceStr.trim() : "0");
        int stockQty = Integer.parseInt(stockStr != null && !stockStr.trim().isEmpty() ? stockStr.trim() : "0");

        ProductDTO dto = ProductDTO.builder()
                .id(id)
                .sellerId(seller.getId())
                .name(name)
                .description(description)
                .price(price)
                .stockQty(stockQty)
                .category(category)
                .imageUrl(imageUrl)
                .build();

        productService.updateProduct(seller.getId(), dto);
        req.getSession().setAttribute("sellerSuccess", "Product listing updated successfully!");
        resp.sendRedirect(req.getContextPath() + "/seller/dashboard");
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp, User seller)
            throws IOException {
        Long id = Long.parseLong(req.getParameter("id"));
        productService.deleteProduct(seller.getId(), id);
        req.getSession().setAttribute("sellerSuccess", "Product listing deleted successfully!");
        resp.sendRedirect(req.getContextPath() + "/seller/dashboard");
    }

    private void handleApiCreate(HttpServletRequest req, HttpServletResponse resp, User seller)
            throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            ProductDTO dto = JsonUtil.fromJson(req.getReader(), ProductDTO.class);
            ProductDTO created = productService.createProduct(seller.getId(), dto);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.success(created)));
        } catch (ValidationException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.error("VALIDATION_ERROR", e.getMessage())));
        }
    }

    private User getAuthenticatedSeller(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute(AuthFilter.SESSION_USER_KEY) : null;
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login?redirect=" + req.getRequestURI());
            return null;
        }
        if (user.getRole() != Role.SELLER && user.getRole() != Role.ADMIN) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Seller privileges required");
            return null;
        }
        return user;
    }
}
