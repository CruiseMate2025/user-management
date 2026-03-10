package org.genc.usermgmt.resource;

import org.genc.usermgmt.dto.CustomUserDetails;
import org.genc.usermgmt.filter.JwtAuthenticationFilter;
import org.genc.usermgmt.service.impl.CustomUserDetailsService;
import org.genc.usermgmt.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = AuthController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class),
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private CustomUserDetailsService userDetailsService; // Use your specific implementation

    @MockitoBean
    private JwtUtil jwtUtil;

    private static final String LOGIN_URL = "/api/v1/userservice/auth/login";

    @Test
    @DisplayName("POST /login - Success Path")
    void testLoginSuccess() throws Exception {
        // Arrange
        String username = "cruise_admin";
        String token = "mocked-jwt-token";

        CustomUserDetails userDetails = Mockito.mock(CustomUserDetails.class);
        when(userDetails.getUsername()).thenReturn(username);

        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn(token);
        when(authenticationManager.authenticate(any())).thenReturn(null);

        String loginJson = "{\"username\":\"" + username + "\", \"password\":\"password123\"}";

        // Act & Assert
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value(token))
                .andExpect(jsonPath("$.role").exists())
                .andExpect(jsonPath("$.appInstance").exists());
    }

    @Test
    @DisplayName("POST /login - Bad Credentials (401)")
    void testLoginBadCredentials() throws Exception {
        // Arrange
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        String loginJson = "{\"username\":\"user\", \"password\":\"wrong_pass\"}";

        // Act & Assert
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isUnauthorized())
                // Ensure the field matches your AuthController @ExceptionHandler or try-catch block
                .andExpect(jsonPath("$.message").value(containsString("Invalid")));
    }

    @Test
    @DisplayName("POST /login - Server Error (500)")
    void testLoginServerError() throws Exception {
        // Arrange
        when(authenticationManager.authenticate(any()))
                .thenThrow(new RuntimeException("Database Connection Failed"));

        String loginJson = "{\"username\":\"user\", \"password\":\"pass\"}";

        // Act & Assert
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value(containsString("Authentication error")));
    }
}