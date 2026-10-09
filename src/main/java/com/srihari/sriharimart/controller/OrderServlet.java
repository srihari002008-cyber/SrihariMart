package com.srihari.sriharimart.controller;

import com.srihari.sriharimart.dao.DAOFactory;
import com.srihari.sriharimart.dto.ApiResponse;
import com.srihari.sriharimart.dto.CartResponseDTO;
import com.srihari.sriharimart.dto.OrderRequestDTO;
import com.srihari.sriharimart.dto.OrderResponseDTO;
import com.srihari.sriharimart.exception.ResourceNotFoundException;
import com.srihari.sriharimart.exception.ValidationException;
import com.srihari.sriharimart.filter.AuthFilter;
import com.srihari.sriharimart.model.User;
import com.srihari.sriharimart.service.CartService;
import com.srihari.sriharimart.service.OrderService;
import com.srihari.sriharimart.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Controller managing checkout with mock payment confirmation and order history.
 */
@WebServlet(name = "OrderServlet", urlPatterns = {
        "/checkout",
        "/checkout/place",
        "/order-confirmation",
        "/orders",
        "/api/v1/orders",
        "/api/v1/orders/checkout",
        "/api/v1/orders/*"
})
public class OrderServlet extends HttpServlet {

    private final OrderService orderService = new OrderService(
            DAOFactory.getOrderDAO(),
            DAOFactory.getCartDAO()
    );

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
        String pathInfo = req.getPathInfo();

        if ("/checkout".equals(servletPath)) {
            CartResponseDTO cart = cartService.getCart(user.getId());
            if (cart.getItems().isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/cart");
                return;
            }
            req.setAttribute("cart", cart);
            req.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(req, resp);

        } else if ("/order-confirmation".equals(servletPath)) {
            String orderIdStr = req.getParameter("orderId");
            if (orderIdStr != null) {
                try {
                    Long orderId = Long.parseLong(orderIdStr);
                    OrderResponseDTO order = orderService.getOrderById(orderId);
                    req.setAttribute("order", order);
                } catch (Exception ignored) {
                }
            }
            req.getRequestDispatcher("/WEB-INF/views/order-confirmation.jsp").forward(req, resp);

        } else if ("/orders".equals(servletPath)) {
            List<OrderResponseDTO> orders = orderService.getBuyerOrders(user.getId());
            req.setAttribute("orders", orders);
            req.getRequestDispatcher("/WEB-INF/views/orders.jsp").forward(req, resp);

        } else if ("/api/v1/orders".equals(servletPath) && (pathInfo == null || "/".equals(pathInfo))) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            List<OrderResponseDTO> orders = orderService.getBuyerOrders(user.getId());
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.success(orders)));

        } else if ("/api/v1/orders".equals(servletPath) && pathInfo != null && pathInfo.length() > 1) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            try {
                Long orderId = Long.parseLong(pathInfo.substring(1));
                OrderResponseDTO order = orderService.getOrderById(orderId);
                resp.getWriter().write(JsonUtil.toJson(ApiResponse.success(order)));
            } catch (ResourceNotFoundException e) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write(JsonUtil.toJson(ApiResponse.error("NOT_FOUND", e.getMessage())));
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(JsonUtil.toJson(ApiResponse.error("BAD_REQUEST", "Invalid order ID")));
            }
        }
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

        if ("/checkout/place".equals(servletPath)) {
            handleWebCheckout(req, resp, user);
        } else if ("/api/v1/orders/checkout".equals(servletPath)) {
            handleApiCheckout(req, resp, user);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleWebCheckout(HttpServletRequest req, HttpServletResponse resp, User user)
            throws ServletException, IOException {
        String shippingAddress = req.getParameter("shippingAddress");
        String paymentMethod = req.getParameter("paymentMethod");

        try {
            OrderRequestDTO dto = new OrderRequestDTO(shippingAddress, paymentMethod);
            OrderResponseDTO placedOrder = orderService.checkout(user.getId(), dto);

            resp.sendRedirect(req.getContextPath() + "/order-confirmation?orderId=" + placedOrder.getId());
        } catch (ValidationException e) {
            req.setAttribute("errorMessage", e.getMessage());
            CartResponseDTO cart = cartService.getCart(user.getId());
            req.setAttribute("cart", cart);
            req.setAttribute("shippingAddress", shippingAddress);
            req.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(req, resp);
        }
    }

    private void handleApiCheckout(HttpServletRequest req, HttpServletResponse resp, User user)
            throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            OrderRequestDTO dto = JsonUtil.fromJson(req.getReader(), OrderRequestDTO.class);
            OrderResponseDTO order = orderService.checkout(user.getId(), dto);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.success(order)));
        } catch (ValidationException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.error("VALIDATION_ERROR", e.getMessage())));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.error("SERVER_ERROR", "Failed to place order")));
        }
    }

    private User getAuthenticatedUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return null;
        }
        return (User) session.getAttribute(AuthFilter.SESSION_USER_KEY);
    }
}
