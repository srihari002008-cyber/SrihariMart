package com.srihari.sriharimart.controller;

import com.srihari.sriharimart.dao.DAOFactory;
import com.srihari.sriharimart.dto.ApiResponse;
import com.srihari.sriharimart.dto.LoginRequestDTO;
import com.srihari.sriharimart.dto.RegisterRequestDTO;
import com.srihari.sriharimart.dto.UserResponseDTO;
import com.srihari.sriharimart.exception.AuthenticationException;
import com.srihari.sriharimart.exception.ValidationException;
import com.srihari.sriharimart.filter.AuthFilter;
import com.srihari.sriharimart.model.Role;
import com.srihari.sriharimart.model.User;
import com.srihari.sriharimart.service.UserService;
import com.srihari.sriharimart.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Controller handling user registration, authentication, and session management.
 * Complies with Section 4 Rule 3 (session regeneration & explicit timeout).
 */
@WebServlet(name = "AuthServlet", urlPatterns = {
        "/login",
        "/register",
        "/logout",
        "/api/v1/auth/login",
        "/api/v1/auth/register",
        "/api/v1/auth/logout",
        "/api/v1/auth/me"
})
public class AuthServlet extends HttpServlet {

    private static final int SESSION_TIMEOUT_SECONDS = 30 * 60; // 30 minutes explicit timeout
    private final UserService userService = new UserService(DAOFactory.getUserDAO());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String servletPath = req.getServletPath();

        if ("/login".equals(servletPath)) {
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
        } else if ("/register".equals(servletPath)) {
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
        } else if ("/logout".equals(servletPath)) {
            handleLogout(req, resp, false);
        } else if ("/api/v1/auth/me".equals(servletPath)) {
            handleApiMe(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String servletPath = req.getServletPath();

        if ("/login".equals(servletPath)) {
            handleFormLogin(req, resp);
        } else if ("/register".equals(servletPath)) {
            handleFormRegister(req, resp);
        } else if ("/logout".equals(servletPath)) {
            handleLogout(req, resp, false);
        } else if ("/api/v1/auth/login".equals(servletPath)) {
            handleApiLogin(req, resp);
        } else if ("/api/v1/auth/register".equals(servletPath)) {
            handleApiRegister(req, resp);
        } else if ("/api/v1/auth/logout".equals(servletPath)) {
            handleLogout(req, resp, true);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleFormLogin(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String redirect = req.getParameter("redirect");

        try {
            LoginRequestDTO dto = new LoginRequestDTO(email, password);
            User user = userService.login(dto);

            // Establish secure session with ID regeneration and explicit timeout
            setupUserSession(req, user);

            if (user.getRole() == Role.SELLER) {
                resp.sendRedirect(req.getContextPath() + "/seller/dashboard");
            } else if (redirect != null && !redirect.trim().isEmpty() && !redirect.contains("://")) {
                resp.sendRedirect(redirect);
            } else {
                resp.sendRedirect(req.getContextPath() + "/products");
            }

        } catch (AuthenticationException | ValidationException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.setAttribute("email", email);
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
        }
    }

    private void handleFormRegister(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String role = req.getParameter("role");

        try {
            RegisterRequestDTO dto = new RegisterRequestDTO(name, email, password, role);
            userService.register(dto);
            resp.sendRedirect(req.getContextPath() + "/login?registered=true");
        } catch (ValidationException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.setAttribute("fieldErrors", e.getFieldErrors());
            req.setAttribute("name", name);
            req.setAttribute("email", email);
            req.setAttribute("selectedRole", role);
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
        }
    }

    private void handleApiLogin(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            LoginRequestDTO dto = JsonUtil.fromJson(req.getReader(), LoginRequestDTO.class);
            User user = userService.login(dto);

            setupUserSession(req, user);

            UserResponseDTO resDto = UserResponseDTO.fromEntity(user);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.success(resDto)));
        } catch (AuthenticationException e) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.error("UNAUTHORIZED", e.getMessage())));
        } catch (ValidationException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.error("VALIDATION_ERROR", e.getMessage())));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.error("SERVER_ERROR", "An unexpected error occurred")));
        }
    }

    private void handleApiRegister(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            RegisterRequestDTO dto = JsonUtil.fromJson(req.getReader(), RegisterRequestDTO.class);
            UserResponseDTO created = userService.register(dto);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.success(created)));
        } catch (ValidationException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.error("VALIDATION_ERROR", e.getMessage())));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.error("SERVER_ERROR", "An unexpected error occurred")));
        }
    }

    private void handleApiMe(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute(AuthFilter.SESSION_USER_KEY) : null;

        if (user != null) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.success(UserResponseDTO.fromEntity(user))));
        } else {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.error("UNAUTHORIZED", "Not authenticated")));
        }
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp, boolean isApi)
            throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        if (isApi) {
            resp.setContentType("application/json");
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.success("Logged out successfully")));
        } else {
            resp.sendRedirect(req.getContextPath() + "/login?loggedOut=true");
        }
    }

    /**
     * Prevents session fixation by regenerating session ID, and sets explicit session timeout.
     */
    private void setupUserSession(HttpServletRequest req, User user) {
        HttpSession oldSession = req.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }
        HttpSession newSession = req.getSession(true);
        newSession.setMaxInactiveInterval(SESSION_TIMEOUT_SECONDS);
        newSession.setAttribute(AuthFilter.SESSION_USER_KEY, user);
        newSession.setAttribute("userDTO", UserResponseDTO.fromEntity(user));
    }
}
