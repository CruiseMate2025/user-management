package org.genc.usermgmt.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.genc.usermgmt.dto.RoleRequestDTO;
import org.genc.usermgmt.dto.RoleResponseDTO;
import org.genc.usermgmt.enums.RoleType;
import org.genc.usermgmt.exception.UserNotFoundException;
import org.genc.usermgmt.security.CustomAccessDeniedHandler;
import org.genc.usermgmt.security.CustomAuthenticationEntryPoint;
import org.genc.usermgmt.service.api.RoleService;
import org.genc.usermgmt.service.impl.CustomUserDetailsService;
import org.genc.usermgmt.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoleController.class)
@AutoConfigureMockMvc(addFilters = false) // Crucial: Bypasses the Security Filter Chain to avoid dot-parsing errors
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RoleService roleService;

    /**
     * These @MockitoBeans are mandatory.
     * Even with filters disabled, Spring Boot looks for these definitions
     * to satisfy the context requirements of your CruiseMate application.
     */
    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @MockitoBean
    private CustomAuthenticationEntryPoint authenticationEntryPoint;

    @MockitoBean
    private CustomAccessDeniedHandler accessDeniedHandler;

    private static final String BASE_URL = "/api/v1/userservice/roles";

    // --- Helper Methods ---

    private RoleResponseDTO createMockResponse(Long id, String name) {
        return RoleResponseDTO.builder()
                .id(id)
                .name(name)
                .description("Test Description")
                .build();
    }

    // --- Test Cases ---

    @Test
    @DisplayName("POST /roles - Success")
    void testCreateRole_Success() throws Exception {
        RoleRequestDTO request = RoleRequestDTO.builder()
                .name(RoleType.CREW)
                .description(RoleType.CREW.getDescription())
                .build();

        Mockito.when(roleService.createRole(any(RoleRequestDTO.class)))
                .thenReturn(createMockResponse(3L, "CREW"));

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(3L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("CREW"));
    }

    @Test
    @DisplayName("GET /roles/{id} - Success")
    void testGetRole_Success() throws Exception {
        Mockito.when(roleService.getRoleById(1L))
                .thenReturn(createMockResponse(1L, "ADMIN"));

        // Hardcoded path variable '1' to prevent URI template parsing issues
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("ADMIN"));
    }

    @Test
    @DisplayName("GET /roles/{id} - Not Found")
    void testGetRole_NotFound() throws Exception {
        Long roleId = 99L;
        String errorMsg = "Role not found with ID: 99";

        Mockito.when(roleService.getRoleById(roleId))
                .thenThrow(new UserNotFoundException(errorMsg));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/99"))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(errorMsg));
    }

    @Test
    @DisplayName("GET /roles - Success")
    void testGetAllRoles_Success() throws Exception {
        Mockito.when(roleService.getAllRoles())
                .thenReturn(Collections.singletonList(createMockResponse(1L, "PASSENGER")));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(1));
    }

    @Test
    @DisplayName("PUT /roles/{id} - Success")
    void testUpdateRole_Success() throws Exception {
        RoleRequestDTO request = RoleRequestDTO.builder()
                .name(RoleType.ADMIN)
                .description("Updated")
                .build();

        Mockito.when(roleService.updateRole(eq(1L), any(RoleRequestDTO.class)))
                .thenReturn(createMockResponse(1L, "ADMIN"));

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /roles/{id} - Success")
    void testDeleteRole_Success() throws Exception {
        Mockito.doNothing().when(roleService).deleteRole(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/1"))
                .andExpect(status().isNoContent());
    }
}