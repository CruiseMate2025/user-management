package org.genc.usermgmt.service.impl;

import org.genc.usermgmt.dto.AdminUpdateRequestDTO;
import org.genc.usermgmt.dto.AdminUpdateResponseDTO;
import org.genc.usermgmt.entity.Role;
import org.genc.usermgmt.entity.User;
import org.genc.usermgmt.enums.RoleType;
import org.genc.usermgmt.repo.UserRepository;
import org.genc.usermgmt.service.api.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserMgmtServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleService roleService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserMgmtServiceImpl userService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setName(RoleType.PASSENGER);
        role.setDescription("Traveler");

        mockUser = User.builder()
                .id(1L)
                .username("cruise_user")
                .fullName("John Doe")
                .roles(role)
                .loyaltyPoints(100)
                .build();
    }

    @Test
    @DisplayName("updateUser - Success (Fixed Assertion)")
    void updateUser_Success() {
        AdminUpdateRequestDTO updateReq = new AdminUpdateRequestDTO();
        updateReq.setFullName("Updated Name");
        updateReq.setEmail("new@genc.org");

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        // FIX: Return the actual object being saved so the changes are reflected in the response
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AdminUpdateResponseDTO response = userService.updateUser(1L, updateReq);

        assertEquals("Updated Name", response.getFullName());
        assertEquals("new@genc.org", response.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("addLoyaltyPoints - Success")
    void addLoyaltyPoints_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        int result = userService.addLoyaltyPoints(1L, 50);

        assertEquals(150, result);
    }
}