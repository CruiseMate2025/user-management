package org.genc.usermgmt.repo;

import jakarta.persistence.*;
import lombok.*;
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
        // Arrange - Builder now takes RoleType directly
        Role adminRole = Role.builder()
                .name(RoleType.ADMIN)
                .description(RoleType.ADMIN.getDescription())
                .build();

        // Act
        Role savedRole = roleRepository.save(adminRole);

        // Assert - Assertion now compares against RoleType enum constant
        assertThat(savedRole).isNotNull();
        assertThat(savedRole.getId()).isNotNull();
        assertThat(savedRole.getName()).isEqualTo(RoleType.ADMIN);
    }

    @Test
    void testFindByName_whenRoleExists_shouldReturnRole() {
        // Arrange: Save a role first
        RoleType roleTypeToFind = RoleType.ROLE_USER;
        // Builder now takes RoleType directly
        Role userRole = Role.builder()
                .name(roleTypeToFind)
                .description(roleTypeToFind.getDescription())
                .build();
        roleRepository.save(userRole);

        // Act
        Optional<Role> foundRole = roleRepository.findByName(roleTypeToFind);

        // Assert - Assertion now compares against RoleType enum constant
        assertThat(foundRole).isPresent();
        assertThat(foundRole.get().getName()).isEqualTo(roleTypeToFind);
        assertThat(foundRole.get().getDescription()).isEqualTo(roleTypeToFind.getDescription());
    }

    @Test
    void testFindByName_whenRoleDoesNotExist_shouldReturnEmptyOptional() {
        // Arrange (database is empty for the name)
        RoleType roleTypeToFind = RoleType.ADMIN;

        // Act
        Optional<Role> foundRole = roleRepository.findByName(roleTypeToFind);

        // Assert
        assertThat(foundRole).isEmpty();
    }

    @Test
    void testFindById_shouldReturnRole() {
        // Arrange: Save a role
        RoleType roleType = RoleType.ROLE_DEV;
        // Builder now takes RoleType directly
        Role devRole = Role.builder()
                .name(roleType)
                .description(roleType.getDescription())
                .build();
        Role savedRole = roleRepository.save(devRole);

        // Act
        Optional<Role> foundRole = roleRepository.findById(savedRole.getId());

        // Assert - Assertion now compares against RoleType enum constant
        assertThat(foundRole).isPresent();
        assertThat(foundRole.get().getName()).isEqualTo(roleType);
    }
}
