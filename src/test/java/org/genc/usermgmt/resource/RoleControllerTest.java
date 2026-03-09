package org.genc.usermgmt.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.genc.usermgmt.config.SecurityConfig;
import org.genc.usermgmt.dto.RoleRequestDTO;
import org.genc.usermgmt.dto.RoleResponseDTO;
import org.genc.usermgmt.enums.RoleType;
import org.genc.usermgmt.exception.UserNotFoundException;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoleController.class)
@Import(SecurityConfig.class)
class RoleControllerTest { // Default package visibility for JUnit 5

    private static final String BASE_URL = "/api/v1/userservice/roles";
    private static final String ROLE_NOT_FOUND = "Role not found with ID: ";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RoleService roleService;

    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private CustomAuthenticationEntryPoint authenticationEntryPoint;
    @MockitoBean
    private CustomAccessDeniedHandler accessDeniedHandler;
    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    // --- Helper Methods ---

    private RoleResponseDTO getRoleResponseDTO(Long id, String name) {
        return RoleResponseDTO.builder()
                .id(id)
                .name(name)
                .description("Test role description")
                .build();
    }

    private RoleRequestDTO getRoleRequestDTO(RoleType type) {
        return RoleRequestDTO.builder()
                .name(type)
                .description(type.getDescription())
                .build();
    }

    // --- Tests ---

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testCreateRole_shouldReturn201Created() throws Exception {
        RoleRequestDTO request = getRoleRequestDTO(RoleType.CREW);
        RoleResponseDTO response = getRoleResponseDTO(3L, RoleType.CREW.getName());

        Mockito.when(roleService.createRole(any(RoleRequestDTO.class))).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(3L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("CREW"));
    }

    @Test
    @WithMockUser(roles = {"PASSENGER"})
    void testCreateRole_asPassenger_shouldReturn403Forbidden() throws Exception {
        RoleRequestDTO request = getRoleRequestDTO(RoleType.ADMIN);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetRole_shouldReturn404NotFound() throws Exception {
        Long roleId = 99L;

        // Use specific domain exception instead of generic RuntimeException
        Mockito.when(roleService.getRoleById(roleId))
                .thenThrow(new UserNotFoundException(ROLE_NOT_FOUND + roleId));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/{id}", roleId))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("404"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(ROLE_NOT_FOUND + roleId));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testDeleteRole_shouldReturn204NoContent() throws Exception {
        Long roleId = 1L;

        Mockito.doNothing().when(roleService).deleteRole(roleId);

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/{id}", roleId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNoContent());
    }
}