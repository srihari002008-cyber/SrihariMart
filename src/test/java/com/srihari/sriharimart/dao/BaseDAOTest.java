package com.srihari.sriharimart.dao;

import com.srihari.sriharimart.util.DBConnectionManager;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Base test class establishing embedded H2 in-memory test database per Section 9.
 */
public abstract class BaseDAOTest {

    private static HikariDataSource testDataSource;

    @BeforeAll
    public static void setUpDatabase() throws Exception {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.h2.Driver");
        config.setJdbcUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL");
        config.setUsername("sa");
        config.setPassword("");
        config.setMaximumPoolSize(5);

        testDataSource = new HikariDataSource(config);
        DBConnectionManager.setDataSource(testDataSource);
    }

    @BeforeEach
    public void resetSchema() throws Exception {
        try (Connection conn = testDataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP ALL OBJECTS");
            executeSqlScript(conn, "db/schema.sql");
            executeSqlScript(conn, "db/seed.sql");
        }
    }

    @AfterAll
    public static void tearDown() {
        if (testDataSource != null && !testDataSource.isClosed()) {
            testDataSource.close();
        }
    }

    private static void executeSqlScript(Connection conn, String scriptPath) throws Exception {
        try (InputStream in = BaseDAOTest.class.getClassLoader().getResourceAsStream(scriptPath)) {
            if (in == null) {
                throw new IllegalStateException("Script not found: " + scriptPath);
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
