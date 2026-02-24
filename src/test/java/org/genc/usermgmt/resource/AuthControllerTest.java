package org.genc.usermgmt.resource;

import org.genc.usermgmt.dto.CustomUserDetails;
import org.genc.usermgmt.filter.JwtAuthenticationFilter;
import org.genc.usermgmt.util.JwtUtil;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
    private UserDetailsService userDetailsService;
    @MockitoBean
    private JwtUtil jwtUtil;
    private static final String LOGIN_URL = "/api/v1/userservice/login";


    @Test
    void testLoginSuccess() throws Exception {
        String username = "user";
        String password = "pass";
        String token = "jwt";
        CustomUserDetails userDetails = Mockito.mock(CustomUserDetails.class);
        Mockito.when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        Mockito.when(jwtUtil.generateToken(userDetails)).thenReturn(token);
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(null);
        String json = "{" +
                "\"username\":\"user\"," +
                "\"password\":\"pass\"}";
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.jwt").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.role").exists());
    }


    @Test
    public void testLoginBadCredentials() throws Exception {
        Mockito.doThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"))
                .when(authenticationManager).authenticate(Mockito.any());
        String json = "{" +
                "\"username\":\"user\"," +
                "\"password\":\"wrong\"}";
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid username or password"));
    }


    @Test
    public void testLoginServerError() throws Exception {
        Mockito.doThrow(new RuntimeException("Server error"))
                .when(authenticationManager).authenticate(Mockito.any());
        String json = "{" +
                "\"username\":\"user\"," +
                "\"password\":\"pass\"}";
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Authentication error")));
    }
}
