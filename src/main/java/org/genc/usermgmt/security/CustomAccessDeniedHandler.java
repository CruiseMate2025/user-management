package org.genc.usermgmt.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.genc.usermgmt.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handles authorization errors (403 Forbidden).
 * This is triggered by the ExceptionTranslationFilter when an authenticated user
 * attempts to access a resource they are not authorized for (e.g., missing a required role).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        log.warn("Access denied for user {}: {}",
                request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "Unknown",
                accessDeniedException.getMessage());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403 Forbidden
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.FORBIDDEN,
                "Access Denied: You do not have permission to access this resource.",
                request.getRequestURI()
        );
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}
