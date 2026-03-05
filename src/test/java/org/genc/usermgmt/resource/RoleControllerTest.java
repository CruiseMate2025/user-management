package org.genc.usermgmt.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.genc.usermgmt.config.SecurityConfig;
import org.genc.usermgmt.dto.RoleRequestDTO;
import org.genc.usermgmt.dto.RoleResponseDTO;
import org.genc.usermgmt.enums.RoleType;
import org.genc.usermgmt.security.CustomAccessDeniedHandler;
import org.genc.usermgmt.security.CustomAuthenticationEntryPoint;
import org.genc.usermgmt.service.api.RoleService;
import org.genc.usermgmt.service.impl.CustomUserDetailsService;
import org.genc.usermgmt.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoleController.class)
@Import(SecurityConfig.class)
// Note: If 403 fails, add @ActiveProfiles("test") and use the testFilterChain in SecurityConfig
public class RoleControllerTest {

    private static final String BASE_URL = "/api/v1/userservice/roles";

    @Autowired
    private MockMvc mockMvc;

    // Helper to convert objects to JSON
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RoleService roleService;

    // Mocked security infrastructure dependencies
    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private CustomAuthenticationEntryPoint authenticationEntryPoint;
    @MockitoBean
    private CustomAccessDeniedHandler accessDeniedHandler;
    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    // --- Helper Methods ---

    private RoleResponseDTO getRoleResponseDTO(Long id) {
        return RoleResponseDTO.builder().id(id).name("ROLE_TEST_" + id).description("TEST role").build();
    }

    private RoleRequestDTO getRoleRequestDTO() {
        return RoleRequestDTO.builder().name(RoleType.ROLE_DEV).description("New role description").build();
    }

    // --- 1. POST /roles (createRole) ---

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testCreateRole_shouldReturn201Created() throws Exception {
        RoleRequestDTO request = getRoleRequestDTO();
        RoleResponseDTO response = getRoleResponseDTO(2L);

        Mockito.when(roleService.createRole(any(RoleRequestDTO.class))).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()) // HTTP 201
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(2L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("ROLE_TEST_2"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testCreateRole_shouldReturn500InternalServerError() throws Exception {
        RoleRequestDTO request = getRoleRequestDTO();

        // Simulate a service failure (e.g., DB down, unexpected exception)
        Mockito.when(roleService.createRole(any(RoleRequestDTO.class)))
                .thenThrow(new RuntimeException("Database connectivity issue")); // Changed to RuntimeException

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError()) // HTTP 500
                // FIX: Expect numerical 500 in 'status', and check descriptive 'error' field
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(500))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Internal Server Error"));
    }

    // --- 2. GET /roles/{id} (getRole) ---

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testGetRole_shouldReturn200Ok() throws Exception {
        Long roleId = 1L;
        RoleResponseDTO response = getRoleResponseDTO(roleId);

        Mockito.when(roleService.getRoleById(roleId)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/{id}", roleId))
                .andExpect(status().isOk()) // HTTP 200
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(roleId));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testGetRole_shouldReturn404NotFound() throws Exception {
        Long roleId = 99L;

        // Simulate a role not found exception (using RuntimeException for Mockito compatibility)
        Mockito.when(roleService.getRoleById(roleId))
                .thenThrow(new RuntimeException("Role not found with ID: " + roleId)); // Changed to RuntimeException

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/{id}", roleId))
                .andExpect(status().isNotFound()) // HTTP 404
                // FIX: Expect numerical 404 in 'status', and check descriptive 'error' field
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Not Found"));
    }

    // --- 3. GET /roles (getAllRoles) ---

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testGetAllRoles_shouldReturn200OkWithData() throws Exception {
        // Renamed and using the more explicit assertion for clarity
        List<RoleResponseDTO> mockList = List.of(getRoleResponseDTO(1L), getRoleResponseDTO(2L));

        Mockito.when(roleService.getAllRoles()).thenReturn(mockList);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL))
                .andExpect(status().isOk()) // HTTP 200
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1L));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testGetAllRoles_shouldReturn200OkWithEmptyList() throws Exception {

        Mockito.when(roleService.getAllRoles()).thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL))
                .andExpect(status().isOk()) // HTTP 200
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));
    }

    // --- 4. PUT /roles/{id} (updateRole) ---

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testUpdateRole_shouldReturn200Ok() throws Exception {
        Long roleId = 1L;
        RoleRequestDTO request = getRoleRequestDTO();
        RoleResponseDTO updatedResponse = getRoleResponseDTO(roleId);

        Mockito.when(roleService.updateRole(anyLong(), any(RoleRequestDTO.class))).thenReturn(updatedResponse);

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/{id}", roleId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()) // HTTP 200
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(roleId));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testUpdateRole_shouldReturn404NotFound() throws Exception {
        Long roleId = 99L;
        RoleRequestDTO request = getRoleRequestDTO();

        // Throwing RuntimeException to satisfy Mockito
        Mockito.when(roleService.updateRole(anyLong(), any(RoleRequestDTO.class)))
                .thenThrow(new RuntimeException("Role not found during update.")); // Changed to RuntimeException

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/{id}", roleId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound()) // HTTP 404
                // FIX: Expect numerical 404 in 'status', and check descriptive 'error' field
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Not Found"));
    }

    // --- 5. DELETE /roles/{id} (deleteRole) ---

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testDeleteRole_shouldReturn204NoContent() throws Exception {
        Long roleId = 1L;

        // Mock to do nothing, indicating success
        Mockito.doNothing().when(roleService).deleteRole(roleId);

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/{id}", roleId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNoContent()); // HTTP 204
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testDeleteRole_shouldReturn404NotFound() throws Exception {
        Long roleId = 99L;

        // Throwing RuntimeException to satisfy Mockito
        Mockito.doThrow(new RuntimeException("Role to delete not found.")) // Changed to RuntimeException
                .when(roleService).deleteRole(roleId);

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/{id}", roleId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound()) // HTTP 404
                // FIX: Expect numerical 404 in 'status', and check descriptive 'error' field
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Not Found"));
    }
}
