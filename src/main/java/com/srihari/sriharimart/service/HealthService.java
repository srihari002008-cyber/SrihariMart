package com.srihari.sriharimart.service;

import com.srihari.sriharimart.util.DBConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Service checking application and database health status.
 */
public class HealthService {

    /**
     * Checks database connectivity.
     *
     * @return Map with "status" and "db" health indicators
     */
    public Map<String, String> checkHealth() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT 1");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                status.put("db", "UP");
            } else {
                status.put("db", "DOWN");
            }
        } catch (Exception e) {
            status.put("db", "DOWN");
            status.put("status", "DOWN");
        }

        return status;
    }
}
