package com.srihari.sriharimart.controller;

import com.srihari.sriharimart.dao.DAOFactory;
import com.srihari.sriharimart.dto.ApiResponse;
import com.srihari.sriharimart.dto.ProductDTO;
import com.srihari.sriharimart.exception.ResourceNotFoundException;
import com.srihari.sriharimart.service.ProductService;
import com.srihari.sriharimart.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Controller serving product catalog browse, search/filter, and detail views.
 */
@WebServlet(name = "ProductServlet", urlPatterns = {
        "",
        "/products",
        "/product-detail",
        "/api/v1/products",
        "/api/v1/products/*",
        "/api/v1/categories"
})
public class ProductServlet extends HttpServlet {

    private final ProductService productService = new ProductService(DAOFactory.getProductDAO());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String servletPath = req.getServletPath();
        String pathInfo = req.getPathInfo();

        if ("/api/v1/categories".equals(servletPath)) {
            handleApiCategories(resp);
            return;
        }

        if ("/api/v1/products".equals(servletPath) && (pathInfo == null || "/".equals(pathInfo))) {
            handleApiProductList(req, resp);
            return;
        }

        if ("/api/v1/products".equals(servletPath) && pathInfo != null && pathInfo.length() > 1) {
            handleApiProductDetail(pathInfo.substring(1), resp);
            return;
        }

        if ("/product-detail".equals(servletPath)) {
            handleWebProductDetail(req, resp);
            return;
        }

        // Default: Home / Product Catalog
        handleWebCatalog(req, resp);
    }

    private void handleWebCatalog(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String category = req.getParameter("category");
        String keyword = req.getParameter("keyword");

        List<ProductDTO> products = productService.searchProducts(category, keyword);
        List<String> categories = productService.getAllCategories();

        req.setAttribute("products", products);
        req.setAttribute("categories", categories);
        req.setAttribute("selectedCategory", category);
        req.setAttribute("keyword", keyword);

        req.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(req, resp);
    }

    private void handleWebProductDetail(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/products");
            return;
        }

        try {
            Long id = Long.parseLong(idStr.trim());
            ProductDTO product = productService.getProductById(id);
            req.setAttribute("product", product);
            req.getRequestDispatcher("/WEB-INF/views/product-detail.jsp").forward(req, resp);
        } catch (ResourceNotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Product not found");
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID");
        }
    }

    private void handleApiProductList(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String category = req.getParameter("category");
        String keyword = req.getParameter("keyword");

        List<ProductDTO> products = productService.searchProducts(category, keyword);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(JsonUtil.toJson(ApiResponse.success(products)));
    }

    private void handleApiProductDetail(String idStr, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            Long id = Long.parseLong(idStr.trim());
            ProductDTO product = productService.getProductById(id);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.success(product)));
        } catch (ResourceNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.error("NOT_FOUND", e.getMessage())));
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.error("BAD_REQUEST", "Invalid product ID format")));
        }
    }

    private void handleApiCategories(HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        List<String> categories = productService.getAllCategories();
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(JsonUtil.toJson(ApiResponse.success(categories)));
    }
}
