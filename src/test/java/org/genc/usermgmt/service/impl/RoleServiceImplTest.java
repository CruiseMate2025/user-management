package org.genc.usermgmt.service.impl;

import org.genc.usermgmt.dto.RoleRequestDTO;
import org.genc.usermgmt.dto.RoleResponseDTO;
import org.genc.usermgmt.entity.Role;
import org.genc.usermgmt.enums.RoleType;
import org.genc.usermgmt.exception.ResourceNotFoundException;
import org.genc.usermgmt.repo.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    // Standardized constants based on your current RoleType enum
    private static final Long ROLE_ID_ADMIN = 1L;
    private static final Long ROLE_ID_PASSENGER = 2L;

    private Role mockAdminRole;
    private RoleRequestDTO adminRequest;

    @BeforeEach
    void setUp() {
        // Setup Admin Role
        mockAdminRole = Role.builder()
                .id(ROLE_ID_ADMIN)
                .name(RoleType.ADMIN)
                .description(RoleType.ADMIN.getDescription())
                .build();

        // Setup Admin Request
        adminRequest = RoleRequestDTO.builder()
                .name(RoleType.ADMIN)
                .description(RoleType.ADMIN.getDescription())
                .build();
    }

    // --- 1. createRole Tests ---

    @Test
    void testCreateRole_shouldCreateNewRole() {
        when(roleRepository.findByName(RoleType.ADMIN)).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenReturn(mockAdminRole);

        RoleResponseDTO result = roleService.createRole(adminRequest);

        assertNotNull(result);
        assertEquals(ROLE_ID_ADMIN, result.getId());
        assertEquals(RoleType.ADMIN.getName(), result.getName());
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void testCreateRole_shouldReturnExistingRole() {
        when(roleRepository.findByName(RoleType.ADMIN)).thenReturn(Optional.of(mockAdminRole));

        RoleResponseDTO result = roleService.createRole(adminRequest);

        assertNotNull(result);
        assertEquals(ROLE_ID_ADMIN, result.getId());
        verify(roleRepository, never()).save(any(Role.class));
    }

    // --- 2. seedRoleData Tests ---

    @Test
    void testSeedRoleData_shouldCreateNewRole() {
        when(roleRepository.findByName(RoleType.ADMIN)).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenReturn(mockAdminRole);

        Role result = roleService.seedRoleData(adminRequest);

        assertNotNull(result);
        assertEquals(RoleType.ADMIN, result.getName());
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    // --- 3. getRoleById Tests ---

    @Test
    void testGetRoleById_shouldReturnRole() {
        when(roleRepository.findById(ROLE_ID_ADMIN)).thenReturn(Optional.of(mockAdminRole));

        RoleResponseDTO result = roleService.getRoleById(ROLE_ID_ADMIN);

        assertNotNull(result);
        assertEquals(ROLE_ID_ADMIN, result.getId());
        verify(roleRepository, times(1)).findById(ROLE_ID_ADMIN);
    }

    @Test
    void testGetRoleById_shouldThrowResourceNotFoundException() {
        when(roleRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleService.getRoleById(99L));
    }

    // --- 4. getAllRoles Tests ---

    @Test
    void testGetAllRoles_shouldReturnListOfRoles() {
        Role mockPassengerRole = Role.builder()
                .id(ROLE_ID_PASSENGER)
                .name(RoleType.PASSENGER)
                .description(RoleType.PASSENGER.getDescription())
                .build();

        when(roleRepository.findAll()).thenReturn(List.of(mockAdminRole, mockPassengerRole));

        List<RoleResponseDTO> results = roleService.getAllRoles();

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(RoleType.ADMIN.getName(), results.get(0).getName());
        assertEquals(RoleType.PASSENGER.getName(), results.get(1).getName());
    }

    // --- 5. updateRole Tests ---

    @Test
    void testUpdateRole_shouldUpdateRoleSuccessfully() {
        // Change Admin to Crew for the update test
        RoleRequestDTO updateRequest = RoleRequestDTO.builder()
                .name(RoleType.CREW)
                .description(RoleType.CREW.getDescription())
                .build();

        when(roleRepository.findById(ROLE_ID_ADMIN)).thenReturn(Optional.of(mockAdminRole));

        Role updatedRole = Role.builder()
                .id(ROLE_ID_ADMIN)
                .name(RoleType.CREW)
                .description(RoleType.CREW.getDescription())
                .build();

        when(roleRepository.save(any(Role.class))).thenReturn(updatedRole);

        RoleResponseDTO result = roleService.updateRole(ROLE_ID_ADMIN, updateRequest);

        assertNotNull(result);
        assertEquals(RoleType.CREW.getName(), result.getName());
        assertEquals(RoleType.CREW.getDescription(), result.getDescription());
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    // --- 6. deleteRole Tests ---

    @Test
    void testDeleteRole_shouldDeleteSuccessfully() {
        when(roleRepository.existsById(ROLE_ID_ADMIN)).thenReturn(true);
        doNothing().when(roleRepository).deleteById(ROLE_ID_ADMIN);

        roleService.deleteRole(ROLE_ID_ADMIN);

        verify(roleRepository, times(1)).deleteById(ROLE_ID_ADMIN);
    }

    // --- 7. getRoleByName Tests ---

    @Test
    void testGetRoleByName_shouldReturnRoleEntity() {
        when(roleRepository.findByName(RoleType.PASSENGER)).thenReturn(Optional.of(
                Role.builder().name(RoleType.PASSENGER).build()
        ));

        Role result = roleService.getRoleByName(RoleType.PASSENGER);

        assertNotNull(result);
        assertEquals(RoleType.PASSENGER, result.getName());
    }
}