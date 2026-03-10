package org.genc.usermgmt.resource;

import org.genc.usermgmt.service.api.UserMgmtService;
import org.genc.usermgmt.service.impl.CustomUserDetailsService;
import org.genc.usermgmt.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProfileController.class)
@TestPropertySource(properties = "api.base-path=/api/v1/userservice")
class ProfileControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private UserMgmtService userMgmtService;
    @MockitoBean private CustomUserDetailsService customUserDetailsService;
    @MockitoBean private JwtUtil jwtUtil;

    @Test
    @WithMockUser
    @DisplayName("GET /loyalty/{userId} - Success")
    void getLoyaltyPoints_Success() throws Exception {
        when(userMgmtService.getLoyaltyPoints(1L)).thenReturn(500);

        mockMvc.perform(get("/api/v1/userservice/profile/loyalty/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loyaltyPoints").value(500));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /loyalty/{userId}/add - Success")
    void addLoyaltyPoints_Success() throws Exception {
        when(userMgmtService.addLoyaltyPoints(1L, 100)).thenReturn(600);

        mockMvc.perform(post("/api/v1/userservice/profile/loyalty/1/add")
                        .with(csrf()) // FIX: Prevents 403
                        .param("points", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loyaltyPoints").value(600));
    }
}