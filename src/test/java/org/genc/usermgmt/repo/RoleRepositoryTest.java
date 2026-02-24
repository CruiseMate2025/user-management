package org.genc.usermgmt.repo;

import org.genc.usermgmt.entity.Role;
import org.genc.usermgmt.enums.RoleType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration Tests for RoleRepository using @DataJpaTest
 */
@DataJpaTest
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void testSaveRole_shouldPersistAndReturnRole() {
        // Arrange - Using ADMIN from the new RoleType enum
        Role adminRole = Role.builder()
                .name(RoleType.ADMIN)
                .description(RoleType.ADMIN.getDescription())
                .build();

        // Act
        Role savedRole = roleRepository.save(adminRole);

        // Assert
        assertThat(savedRole).isNotNull();
        assertThat(savedRole.getId()).isNotNull();
        assertThat(savedRole.getName()).isEqualTo(RoleType.ADMIN);
        assertThat(savedRole.getDescription()).isEqualTo("Administrator Role");
    }

    @Test
    void testFindByName_whenRoleExists_shouldReturnRole() {
        // Arrange: Using PASSENGER instead of the old ROLE_USER
        RoleType roleTypeToFind = RoleType.PASSENGER;
        Role passengerRole = Role.builder()
                .name(roleTypeToFind)
                .description(roleTypeToFind.getDescription())
                .build();
        roleRepository.save(passengerRole);

        // Act
        Optional<Role> foundRole = roleRepository.findByName(roleTypeToFind);

        // Assert
        assertThat(foundRole).isPresent();
        assertThat(foundRole.get().getName()).isEqualTo(roleTypeToFind);
        assertThat(foundRole.get().getDescription()).isEqualTo("Passenger of cruise mate");
    }

    @Test
    void testFindByName_whenRoleDoesNotExist_shouldReturnEmptyOptional() {
        // Arrange: Use a valid enum but ensure it isn't saved in the DB
        RoleType roleTypeToFind = RoleType.ADMIN;

        // Act
        Optional<Role> foundRole = roleRepository.findByName(roleTypeToFind);

        // Assert
        assertThat(foundRole).isEmpty();
    }

    @Test
    void testFindById_shouldReturnRole() {
        // Arrange: Using CREW instead of the old ROLE_DEV
        RoleType roleType = RoleType.CREW;
        Role crewRole = Role.builder()
                .name(roleType)
                .description(roleType.getDescription())
                .build();
        Role savedRole = roleRepository.save(crewRole);

        // Act
        Optional<Role> foundRole = roleRepository.findById(savedRole.getId());

        // Assert
        assertThat(foundRole).isPresent();
        assertThat(foundRole.get().getName()).isEqualTo(roleType);
        assertThat(foundRole.get().getDescription()).isEqualTo("crew of cruise mate");
    }
}