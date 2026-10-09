package com.srihari.sriharimart.dao;

import com.srihari.sriharimart.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for User entities.
 */
public interface UserDAO {

    /**
     * Finds a user by primary key ID.
     *
     * @param id User ID
     * @return Optional containing User if found, empty otherwise
     */
    Optional<User> findById(Long id);

    /**
     * Finds a user by unique email address.
     *
     * @param email User email
     * @return Optional containing User if found, empty otherwise
     */
    Optional<User> findByEmail(String email);

    /**
     * Persists a new user record.
     *
     * @param user User entity to persist
     * @return Persisted user with generated ID
     */
    User save(User user);

    /**
     * Checks if a user already exists with given email.
     *
     * @param email Email to check
     * @return true if exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Retrieves all users.
     *
     * @return List of all users
     */
    List<User> findAll();
}
