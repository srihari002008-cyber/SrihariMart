package com.srihari.sriharimart.listener;

import com.srihari.sriharimart.util.DBConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * ServletContextListener responsible for application startup and shutdown lifecycle.
 * Initializes the HikariCP connection pool, runs database migrations, and releases resources.
 * Complies with Section 4 Rule 5 and Section 14.
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOGGER.info("SrihariMart Application Initializing...");
        try {
            // Initialize HikariCP pool
            DBConnectionManager.initialize();

            // Run database migrations
            runMigrations();

            LOGGER.info("SrihariMart Application Initialized Successfully.");
        } catch (Exception e) {
            LOGGER.error("Failed to initialize SrihariMart application", e);
            throw new RuntimeException("Application startup failure", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("SrihariMart Application Shutting Down...");
        DBConnectionManager.shutdown();
        LOGGER.info("SrihariMart Application Shutdown Complete.");
    }

    private void runMigrations() {
        try (Connection conn = DBConnectionManager.getConnection()) {
            boolean tablesExist = false;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT count(*) FROM INFORMATION_SCHEMA.TABLES WHERE LOWER(TABLE_NAME) = 'users' AND TABLE_SCHEMA = 'PUBLIC'")) {
                if (rs.next() && rs.getInt(1) > 0) {
                    tablesExist = true;
                }
            }

            if (!tablesExist) {
                LOGGER.info("Running migration: V1__init_schema.sql");
                executeSqlScript(conn, "db/migrations/V1__init_schema.sql");

                LOGGER.info("Running migration: V2__seed_data.sql");
                executeSqlScript(conn, "db/migrations/V2__seed_data.sql");
                LOGGER.info("Database schema and seed data initialized successfully.");
            } else {
                LOGGER.info("Database tables already present. Skipping initial migration.");
            }
        } catch (Exception e) {
            LOGGER.error("Database migration failed", e);
            throw new RuntimeException("Database migration failed", e);
        }
    }

    private void executeSqlScript(Connection conn, String scriptPath) throws Exception {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(scriptPath)) {
            if (in == null) {
                throw new IllegalStateException("Migration script not found on classpath: " + scriptPath);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                StringBuilder sql = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("--")) {
                        continue;
                    }
                    sql.append(line).append(" ");
                    if (line.endsWith(";")) {
                        String statementStr = sql.toString().trim();
                        // remove trailing semicolon
                        statementStr = statementStr.substring(0, statementStr.length() - 1);
                        if (!statementStr.isEmpty()) {
                            try (Statement stmt = conn.createStatement()) {
                                stmt.execute(statementStr);
                            }
                        }
                        sql.setLength(0);
                    }
                }
            }
        }
    }
}
