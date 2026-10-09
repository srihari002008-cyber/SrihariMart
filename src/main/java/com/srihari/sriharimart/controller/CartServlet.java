package com.srihari.sriharimart.controller;

import com.srihari.sriharimart.dao.DAOFactory;
import com.srihari.sriharimart.dto.ApiResponse;
import com.srihari.sriharimart.dto.CartResponseDTO;
import com.srihari.sriharimart.exception.ValidationException;
import com.srihari.sriharimart.filter.AuthFilter;
import com.srihari.sriharimart.model.User;
import com.srihari.sriharimart.service.CartService;
import com.srihari.sriharimart.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

/**
 * Controller handling cart management: add, update, remove, and view with running totals.
 */
@WebServlet(name = "CartServlet", urlPatterns = {
        "/cart",
        "/cart/add",
        "/cart/update",
        "/cart/remove",
        "/cart/clear",
        "/api/v1/cart",
        "/api/v1/cart/items",
        "/api/v1/cart/items/*"
})
public class CartServlet extends HttpServlet {

    private final CartService cartService = new CartService(
            DAOFactory.getCartDAO(),
            DAOFactory.getProductDAO()
    );

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = getAuthenticatedUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login?redirect=" + req.getRequestURI());
            return;
        }

        String servletPath = req.getServletPath();
        if ("/api/v1/cart".equals(servletPath)) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            CartResponseDTO cart = cartService.getCart(user.getId());
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.success(cart)));
            return;
        }

        CartResponseDTO cart = cartService.getCart(user.getId());
        req.setAttribute("cart", cart);
        req.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User user = getAuthenticatedUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String servletPath = req.getServletPath();
        String pathInfo = req.getPathInfo();

        try {
            if ("/cart/add".equals(servletPath) || "/api/v1/cart/items".equals(servletPath)) {
                handleAdd(req, resp, user, "/api/v1/cart/items".equals(servletPath));
            } else if ("/cart/update".equals(servletPath)) {
                handleUpdate(req, resp, user);
            } else if ("/cart/remove".equals(servletPath)) {
                handleRemove(req, resp, user);
            } else if ("/cart/clear".equals(servletPath)) {
                cartService.clearCart(user.getId());
                resp.sendRedirect(req.getContextPath() + "/cart");
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (ValidationException e) {
            if (servletPath.startsWith("/api/v1/")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.setContentType("application/json");
                resp.getWriter().write(JsonUtil.toJson(ApiResponse.error("VALIDATION_ERROR", e.getMessage())));
            } else {
                req.getSession().setAttribute("cartErrorMessage", e.getMessage());
                resp.sendRedirect(req.getContextPath() + "/cart");
            }
        }
    }

    private void handleAdd(HttpServletRequest req, HttpServletResponse resp, User user, boolean isApi)
            throws IOException {
        Long productId;
        int quantity = 1;

        if (isApi) {
            Map<?, ?> map = JsonUtil.fromJson(req.getReader(), Map.class);
            productId = ((Number) map.get("productId")).longValue();
            if (map.containsKey("quantity")) {
                quantity = ((Number) map.get("quantity")).intValue();
            }
        } else {
            productId = Long.parseLong(req.getParameter("productId"));
            String qtyStr = req.getParameter("quantity");
            if (qtyStr != null && !qtyStr.trim().isEmpty()) {
                quantity = Integer.parseInt(qtyStr.trim());
            }
        }

        cartService.addToCart(user.getId(), productId, quantity);

        if (isApi) {
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_OK);
            CartResponseDTO cart = cartService.getCart(user.getId());
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.success(cart)));
        } else {
            req.getSession().setAttribute("cartSuccessMessage", "Item added to cart!");
            String redirect = req.getParameter("redirect");
            if (redirect != null && !redirect.trim().isEmpty() && !redirect.contains("WEB-INF")) {
                resp.sendRedirect(redirect);
            } else {
                resp.sendRedirect(req.getContextPath() + "/cart");
            }
        }
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp, User user)
            throws IOException {
        Long cartItemId = Long.parseLong(req.getParameter("cartItemId"));
        int quantity = Integer.parseInt(req.getParameter("quantity"));

        cartService.updateQuantity(user.getId(), cartItemId, quantity);
        resp.sendRedirect(req.getContextPath() + "/cart");
    }

    private void handleRemove(HttpServletRequest req, HttpServletResponse resp, User user)
            throws IOException {
        Long cartItemId = Long.parseLong(req.getParameter("cartItemId"));
        cartService.removeFromCart(user.getId(), cartItemId);
        resp.sendRedirect(req.getContextPath() + "/cart");
    }

    private User getAuthenticatedUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return null;
        }
        return (User) session.getAttribute(AuthFilter.SESSION_USER_KEY);
    }
}
