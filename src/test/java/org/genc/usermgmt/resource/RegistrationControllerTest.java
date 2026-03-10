package org.genc.usermgmt.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.genc.usermgmt.dto.UserRegistrationRequestDTO;
import org.genc.usermgmt.dto.UserRegistrationResponseDTO;
import org.genc.usermgmt.service.api.UserMgmtService;
import org.genc.usermgmt.service.impl.CustomUserDetailsService;
import org.genc.usermgmt.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegistrationController.class)
class RegistrationControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private UserMgmtService userMgmtService;
    @MockitoBean private CustomUserDetailsService customUserDetailsService;
    @MockitoBean private JwtUtil jwtUtil;

    @Test
    @WithMockUser // Usually registration is public, but if security is tight, this bypasses 403
    void registerUser_Success() throws Exception {
        UserRegistrationRequestDTO request = new UserRegistrationRequestDTO();
        request.setUsername("new_user");

        UserRegistrationResponseDTO response = UserRegistrationResponseDTO.builder()
                .username("new_user")
                .userMessage("successfully registered")
                .build();

        when(userMgmtService.registerNewUser(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/userservice/register")
                        .with(csrf()) // FIX: POST requires CSRF
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userMessage").value(containsString("successfully")));
    }
}