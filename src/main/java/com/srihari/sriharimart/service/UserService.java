package com.srihari.sriharimart.service;

import com.srihari.sriharimart.dao.UserDAO;
import com.srihari.sriharimart.dto.LoginRequestDTO;
import com.srihari.sriharimart.dto.RegisterRequestDTO;
import com.srihari.sriharimart.dto.UserResponseDTO;
import com.srihari.sriharimart.exception.AuthenticationException;
import com.srihari.sriharimart.exception.ResourceNotFoundException;
import com.srihari.sriharimart.exception.ValidationException;
import com.srihari.sriharimart.model.Role;
import com.srihari.sriharimart.model.User;
import com.srihari.sriharimart.util.PasswordUtil;
import com.srihari.sriharimart.util.ValidationUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Service orchestrating user registration, authentication, and profile retrieval.
 * Depends on UserDAO interface adhering to SOLID principles.
 */
public class UserService {

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Registers a new user with input validation and BCrypt password hashing.
     *
     * @param req Registration form data
     * @return Created user profile DTO
     * @throws ValidationException on invalid input or duplicate email
     */
    public UserResponseDTO register(RegisterRequestDTO req) {
        Map<String, String> errors = new HashMap<>();

        if (req == null) {
            throw new ValidationException("Registration payload cannot be null");
        }

        if (!ValidationUtil.isNotBlank(req.getName())) {
            errors.put("name", "Full name is required");
        }

        if (!ValidationUtil.isValidEmail(req.getEmail())) {
            errors.put("email", "A valid email address is required");
        }

        if (!ValidationUtil.isValidPassword(req.getPassword())) {
            errors.put("password", "Password must be at least 6 characters");
        }

        Role role = Role.fromString(req.getRole());
        if (role == null || role == Role.ADMIN) {
            errors.put("role", "Valid registration role must be BUYER or SELLER");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed", errors);
        }

        String normalizedEmail = req.getEmail().trim().toLowerCase();
        if (userDAO.existsByEmail(normalizedEmail)) {
            errors.put("email", "Email is already registered");
            throw new ValidationException("Duplicate email", errors);
        }

        String passwordHash = PasswordUtil.hashPassword(req.getPassword().trim());

        User user = new User();
        user.setName(req.getName().trim());
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordHash);
        user.setRole(role);

        User saved = userDAO.save(user);
        return UserResponseDTO.fromEntity(saved);
    }

    /**
     * Authenticates user against BCrypt password hash.
     *
     * @param req Login credentials
     * @return Authenticated User entity
     * @throws AuthenticationException on invalid credentials
     * @throws ValidationException     on missing input
     */
    public User login(LoginRequestDTO req) {
        if (req == null || !ValidationUtil.isNotBlank(req.getEmail()) || !ValidationUtil.isNotBlank(req.getPassword())) {
            throw new ValidationException("Email and password are required");
        }

        String normalizedEmail = req.getEmail().trim().toLowerCase();
        User user = userDAO.findByEmail(normalizedEmail)
                .orElseThrow(() -> new AuthenticationException("Invalid email or password"));

        if (!PasswordUtil.checkPassword(req.getPassword().trim(), user.getPasswordHash())) {
            throw new AuthenticationException("Invalid email or password");
        }

        return user;
    }

    /**
     * Retrieves user profile DTO by user ID.
     *
     * @param id User ID
     * @return UserResponseDTO
     * @throws ResourceNotFoundException if user does not exist
     */
    public UserResponseDTO getUserById(Long id) {
        if (id == null) {
            throw new ValidationException("User ID is required");
        }
        User user = userDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for ID: " + id));
        return UserResponseDTO.fromEntity(user);
    }
}
