package com.srihari.sriharimart.dao.impl;

import com.srihari.sriharimart.dao.UserDAO;
import com.srihari.sriharimart.exception.AppException;
import com.srihari.sriharimart.model.Role;
import com.srihari.sriharimart.model.User;
import com.srihari.sriharimart.util.DBConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of UserDAO using PreparedStatement and connection pooling.
 */
public class UserDAOImpl implements UserDAO {

    private static final String SQL_FIND_BY_ID =
            "SELECT id, name, email, password_hash, role, created_at FROM users WHERE id = ?";

    private static final String SQL_FIND_BY_EMAIL =
            "SELECT id, name, email, password_hash, role, created_at FROM users WHERE LOWER(email) = LOWER(?)";

    private static final String SQL_INSERT =
            "INSERT INTO users (name, email, password_hash, role, created_at) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";

    private static final String SQL_EXISTS_BY_EMAIL =
            "SELECT 1 FROM users WHERE LOWER(email) = LOWER(?) LIMIT 1";

    private static final String SQL_FIND_ALL =
            "SELECT id, name, email, password_hash, role, created_at FROM users ORDER BY id ASC";

    /**
     * Finds a user by primary key ID.
     *
     * @param id User ID
     * @return Optional containing User if found, empty otherwise
     */
    @Override
    public Optional<User> findById(Long id) {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new AppException("Error finding user by ID: " + id, e);
        }
        return Optional.empty();
    }

    /**
     * Finds a user by unique email address.
     *
     * @param email User email
     * @return Optional containing User if found, empty otherwise
     */
    @Override
    public Optional<User> findByEmail(String email) {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_EMAIL)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new AppException("Error finding user by email: " + email, e);
        }
        return Optional.empty();
    }

    /**
     * Persists a new user record.
     *
     * @param user User entity to persist
     * @return Persisted user with generated ID
     */
    @Override
    public User save(User user) {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getRole().name());

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                }
            }
            return user;
        } catch (SQLException e) {
            throw new AppException("Error inserting user: " + user.getEmail(), e);
        }
    }

    /**
     * Checks if a user already exists with given email.
     *
     * @param email Email to check
     * @return true if exists, false otherwise
     */
    @Override
    public boolean existsByEmail(String email) {
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_EXISTS_BY_EMAIL)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new AppException("Error checking email existence: " + email, e);
        }
    }

    /**
     * Retrieves all users.
     *
     * @return List of all users
     */
    @Override
    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            throw new AppException("Error retrieving all users", e);
        }
        return list;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(Role.fromString(rs.getString("role")));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }
}
