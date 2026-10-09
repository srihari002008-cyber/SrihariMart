package com.srihari.sriharimart.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordUtilTest {

    @Test
    void testHashAndCheckPassword() {
        String password = "Password@123";
        String hashed = PasswordUtil.hashPassword(password);

        assertNotNull(hashed);
        assertTrue(hashed.startsWith("$2a$"));
        assertTrue(PasswordUtil.checkPassword(password, hashed));
        assertFalse(PasswordUtil.checkPassword("WrongPassword", hashed));
    }

    @Test
    void testPrintSeedHashes() {
        System.out.println("HASH_ADMIN: " + PasswordUtil.hashPassword("Admin@123"));
        System.out.println("HASH_SELLER: " + PasswordUtil.hashPassword("Seller@123"));
        System.out.println("HASH_BUYER: " + PasswordUtil.hashPassword("Buyer@123"));
    }
}
