package org.genc.usermgmt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.genc.usermgmt.dto.CustomUserDetails;
import org.genc.usermgmt.service.impl.CustomUserDetailsService;
import org.genc.usermgmt.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtAuthenticationFilter filter;
    private JwtUtil jwtUtil;
    private CustomUserDetailsService userDetailsService;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        jwtUtil = mock(JwtUtil.class);
        userDetailsService = mock(CustomUserDetailsService.class);
        filterChain = mock(FilterChain.class);
        filter = new JwtAuthenticationFilter(jwtUtil, userDetailsService);
        SecurityContextHolder.clearContext();
    }

    @Test
    void testShouldNotFilter_LoginPath() throws ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/userservice/auth/login");
        request.setMethod("POST");

        boolean result = filter.shouldNotFilter(request);
        assertTrue(result);
    }

    @Test
    void testDoFilterInternal_ValidToken() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader("Authorization", "Bearer mock-token");
        request.setRequestURI("/api/v1/userservice/profile");

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(jwtUtil.extractUsername("mock-token")).thenReturn("user123");
        when(userDetailsService.loadUserByUsername("user123")).thenReturn(userDetails);
        when(jwtUtil.validateToken("mock-token", userDetails)).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_NoHeader() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setRequestURI("/api/v1/userservice/profile");

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }
}