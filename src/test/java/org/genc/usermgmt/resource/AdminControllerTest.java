package org.genc.usermgmt.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.genc.usermgmt.dto.AdminUpdateRequestDTO;
import org.genc.usermgmt.dto.AdminUpdateResponseDTO;
import org.genc.usermgmt.entity.Role;
import org.genc.usermgmt.entity.User;
import org.genc.usermgmt.repo.UserRepository;
import org.genc.usermgmt.service.api.UserMgmtService;
import org.genc.usermgmt.service.impl.CustomUserDetailsService;
import org.genc.usermgmt.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private UserMgmtService userMgmtService;
    @MockitoBean private UserRepository userRepository;
    @MockitoBean private CustomUserDetailsService customUserDetailsService;
    @MockitoBean private JwtUtil jwtUtil;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /settings/{id} - Success")
    void getAdminSettings_Success() throws Exception {
        Role role = new Role();
        role.setDescription("Admin");
        User mockUser = User.builder().id(1L).username("admin").roles(role).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        mockMvc.perform(get("/api/v1/userservice/admin/settings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PATCH /settings/{id} - Success")
    void updateAdminSettings_Success() throws Exception {
        AdminUpdateRequestDTO request = new AdminUpdateRequestDTO();
        AdminUpdateResponseDTO response = AdminUpdateResponseDTO.builder().fullName("Updated").build();
        when(userMgmtService.updateUser(eq(1L), any())).thenReturn(response);

        mockMvc.perform(patch("/api/v1/userservice/admin/settings/1")
                        .with(csrf()) // FIX: Prevents 403
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Updated"));
    }
}