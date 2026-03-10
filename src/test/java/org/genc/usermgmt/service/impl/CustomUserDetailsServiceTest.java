package org.genc.usermgmt.service.impl;

import org.genc.usermgmt.dto.CustomUserDetails;
import org.genc.usermgmt.entity.Role;
import org.genc.usermgmt.entity.User;
import org.genc.usermgmt.enums.RoleType;
import org.genc.usermgmt.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setName(RoleType.PASSENGER);

        mockUser = User.builder()
                .id(1L)
                .username("cruise_captain")
                .password("encoded_pass")
                .roles(role)
                .build();
    }

    @Test
    @DisplayName("loadUserByUsername - Success Path")
    void loadUserByUsername_Success() {
        // Arrange
        when(userRepository.findByUsername("cruise_captain")).thenReturn(Optional.of(mockUser));

        // Act
        CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername("cruise_captain");

        // Assert
        assertNotNull(userDetails);
        assertEquals("cruise_captain", userDetails.getUsername());
        verify(userRepository, times(1)).findByUsername("cruise_captain");
    }

    @Test
    @DisplayName("loadUserByUsername - User Not Found (Throws Exception)")
    void loadUserByUsername_NotFound() {
        // Arrange
        when(userRepository.findByUsername("unknown_user")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("unknown_user");
        });
    }
}