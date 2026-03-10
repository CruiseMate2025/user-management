package org.genc.usermgmt.util;

import org.genc.usermgmt.dto.CustomUserDetails;
import org.genc.usermgmt.entity.Role;
import org.genc.usermgmt.entity.User;
import org.genc.usermgmt.enums.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private CustomUserDetails userDetails;

    // Secret must be 32+ characters for HS256
    private final String testSecret = "myCruiseMateSecretKeyForJwtGeneration2026!";
    private final Long testExpiration = 900000L;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();

        // Manual injection of @Value fields
        ReflectionTestUtils.setField(jwtUtil, "secret", testSecret);
        ReflectionTestUtils.setField(jwtUtil, "expiration", testExpiration);

        Role role = new Role();
        role.setName(RoleType.PASSENGER);

        User user = User.builder()
                .username("maritime_user")
                .roles(role)
                .build();

        userDetails = new CustomUserDetails(user);
    }

    @Test
    @DisplayName("generateToken - Should return a non-empty string")
    void generateToken_Success() {
        String token = jwtUtil.generateToken(userDetails);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("extractUsername - Should retrieve correct subject")
    void extractUsername_Success() {
        String token = jwtUtil.generateToken(userDetails);
        String username = jwtUtil.extractUsername(token);
        assertEquals("maritime_user", username);
    }

    @Test
    @DisplayName("validateToken - Should return true for matching user and active time")
    void validateToken_Valid() {
        String token = jwtUtil.generateToken(userDetails);
        Boolean isValid = jwtUtil.validateToken(token, userDetails);
        assertTrue(isValid);
    }

    @Test
    @DisplayName("validateToken - Should return false when usernames do not match")
    void validateToken_InvalidUser() {
        String token = jwtUtil.generateToken(userDetails);
        User otherUser = User.builder().username("different_user").build();
        CustomUserDetails otherDetails = new CustomUserDetails(otherUser);

        Boolean isValid = jwtUtil.validateToken(token, otherDetails);
        assertFalse(isValid);
    }

    @Test
    @DisplayName("validateToken - Should return false for malformed tokens (Catch Block)")
    void validateToken_ExceptionPath() {
        Boolean isValid = jwtUtil.validateToken("this.isNot.aValidToken", userDetails);
        assertFalse(isValid);
    }

    @Test
    @DisplayName("extractExpiration - Should extract a future date")
    void extractExpiration_Success() {
        String token = jwtUtil.generateToken(userDetails);
        Date expiry = jwtUtil.extractExpiration(token);
        assertTrue(expiry.after(new Date()));
    }

    @Test
    @DisplayName("extractAndConcatenateRoles - Should return roles with security prefix")
    void extractAndConcatenateRoles_Success() {
        String roles = jwtUtil.extractAndConcatenateRoles(userDetails);
        // Spring Security SimpleGrantedAuthority usually returns "ROLE_NAME"
        assertEquals("ROLE_PASSENGER", roles);
    }

    @Test
    @DisplayName("extractAndConcatenateRoles - Should handle null/empty authorities")
    void extractAndConcatenateRoles_Empty() {
        User emptyUser = new User(); // No roles assigned
        CustomUserDetails emptyDetails = new CustomUserDetails(emptyUser);

        String roles = jwtUtil.extractAndConcatenateRoles(emptyDetails);
        assertEquals("", roles);
    }
}