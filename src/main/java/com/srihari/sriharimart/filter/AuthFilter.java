package com.srihari.sriharimart.filter;

import com.srihari.sriharimart.dto.ApiResponse;
import com.srihari.sriharimart.model.Role;
import com.srihari.sriharimart.model.User;
import com.srihari.sriharimart.util.JsonUtil;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Authentication and authorization filter safeguarding protected routes.
 * Enforces session verification and role-based access control.
 */
public class AuthFilter implements Filter {

    public static final String SESSION_USER_KEY = "currentUser";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getServletPath();
        String pathInfo = req.getPathInfo();
        String fullPath = path + (pathInfo != null ? pathInfo : "");

        boolean isApi = fullPath.startsWith("/api/v1/");

        // Check if route requires authentication
        boolean isProtectedCart = fullPath.startsWith("/cart") || fullPath.startsWith("/api/v1/cart");
        boolean isProtectedOrder = fullPath.startsWith("/checkout") || fullPath.startsWith("/orders") || fullPath.startsWith("/api/v1/orders");
        boolean isSellerRoute = fullPath.startsWith("/seller") || fullPath.startsWith("/api/v1/seller");
        boolean isAdminRoute = fullPath.startsWith("/admin") || fullPath.startsWith("/api/v1/admin");

        if (isProtectedCart || isProtectedOrder || isSellerRoute || isAdminRoute) {
            HttpSession session = req.getSession(false);
            User user = (session != null) ? (User) session.getAttribute(SESSION_USER_KEY) : null;

            if (user == null) {
                if (isApi) {
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.setContentType("application/json");
                    res.getWriter().write(JsonUtil.toJson(ApiResponse.error("UNAUTHORIZED", "User must be logged in")));
                } else {
                    res.sendRedirect(req.getContextPath() + "/login?redirect=" + req.getRequestURI());
                }
                return;
            }

            // Check Seller permissions
            if (isSellerRoute && user.getRole() != Role.SELLER && user.getRole() != Role.ADMIN) {
                if (isApi) {
                    res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    res.setContentType("application/json");
                    res.getWriter().write(JsonUtil.toJson(ApiResponse.error("FORBIDDEN", "Seller role required")));
                } else {
                    res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Seller account required");
                }
                return;
            }

            // Check Admin permissions
            if (isAdminRoute && user.getRole() != Role.ADMIN) {
                if (isApi) {
                    res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    res.setContentType("application/json");
                    res.getWriter().write(JsonUtil.toJson(ApiResponse.error("FORBIDDEN", "Admin role required")));
                } else {
                    res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Admin account required");
                }
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
