package com.srihari.sriharimart.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton database connection manager utilizing HikariCP connection pooling.
 * Lifecycle is controlled by AppContextListener.
 */
public final class DBConnectionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DBConnectionManager.class);
    private static volatile DataSource dataSource;

    private DBConnectionManager() {
        // Prevent instantiation
    }

    /**
     * Initializes connection pool with default or configured properties.
     */
    public static synchronized void initialize() {
        if (dataSource != null) {
            LOGGER.warn("DataSource is already initialized");
            return;
        }

        Properties props = new Properties();
        try (InputStream in = DBConnectionManager.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (Exception e) {
            LOGGER.debug("config.properties not found or could not be loaded: {}", e.getMessage());
        }

        String jdbcUrl = System.getenv("DB_URL");
        if (jdbcUrl == null || jdbcUrl.trim().isEmpty()) {
            jdbcUrl = props.getProperty("db.url", "jdbc:h2:./data/sriharimart;DB_CLOSE_DELAY=-1;MODE=PostgreSQL");
        }

        String user = System.getenv("DB_USER");
        if (user == null || user.trim().isEmpty()) {
            user = props.getProperty("db.user", "sa");
        }

        String password = System.getenv("DB_PASSWORD");
        if (password == null) {
            password = props.getProperty("db.password", "");
        }

        String driver = System.getenv("DB_DRIVER");
        if (driver == null || driver.trim().isEmpty()) {
            driver = props.getProperty("db.driver", "org.h2.Driver");
        }

        HikariConfig config = new HikariConfig();
        config.setDriverClassName(driver);
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(user);
        config.setPassword(password);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(10000);
        config.setPoolName("SrihariMartHikariPool");

        LOGGER.info("Initializing HikariCP pool for JDBC URL: {}", jdbcUrl);
        dataSource = new HikariDataSource(config);
    }

    /**
     * Sets an explicit DataSource (e.g. for unit testing or custom injection).
     *
     * @param customDataSource DataSource to use
     */
    public static synchronized void setDataSource(DataSource customDataSource) {
        dataSource = customDataSource;
    }

    /**
     * Gets DataSource instance.
     *
     * @return active DataSource
     */
    public static DataSource getDataSource() {
        if (dataSource == null) {
            synchronized (DBConnectionManager.class) {
                if (dataSource == null) {
                    initialize();
                }
            }
        }
        return dataSource;
    }

    /**
     * Obtains a connection from the pool. Callers MUST close it via try-with-resources.
     *
     * @return Connection from the pool
     * @throws SQLException on database connection error
     */
    public static Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    /**
     * Gracefully shuts down the HikariCP pool.
     */
    public static synchronized void shutdown() {
        if (dataSource instanceof HikariDataSource hikariDataSource) {
            if (!hikariDataSource.isClosed()) {
                LOGGER.info("Closing HikariCP connection pool");
                hikariDataSource.close();
            }
        }
        dataSource = null;
    }
}
