package com.srihari.sriharimart.controller;

import com.srihari.sriharimart.service.HealthService;
import com.srihari.sriharimart.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Health check endpoint verifying service and database readiness.
 * Returns: { "status": "UP", "db": "UP" } per Section 18.
 */
@WebServlet(name = "HealthServlet", urlPatterns = {"/api/v1/health"})
public class HealthServlet extends HttpServlet {

    private final HealthService healthService = new HealthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Map<String, String> status = healthService.checkHealth();
        if ("UP".equals(status.get("status")) && "UP".equals(status.get("db"))) {
            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        }

        resp.getWriter().write(JsonUtil.toJson(status));
    }
}
