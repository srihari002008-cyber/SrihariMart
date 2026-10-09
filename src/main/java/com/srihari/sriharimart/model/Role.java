package com.srihari.sriharimart.model;

/**
 * User roles in SrihariMart marketplace.
 */
public enum Role {
    BUYER,
    SELLER,
    ADMIN;

    public static Role fromString(String role) {
        if (role == null) {
            return null;
        }
        for (Role r : Role.values()) {
            if (r.name().equalsIgnoreCase(role.trim())) {
                return r;
            }
        }
        return null;
    }
}
