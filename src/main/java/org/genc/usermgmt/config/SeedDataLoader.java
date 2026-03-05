package org.genc.usermgmt.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.genc.usermgmt.dto.RoleRequestDTO;
import org.genc.usermgmt.dto.UserRegistrationRequestDTO;
import org.genc.usermgmt.entity.Role;
import org.genc.usermgmt.enums.RoleType;
import org.genc.usermgmt.service.api.RoleService;
import org.genc.usermgmt.service.api.UserMgmtService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeedDataLoader implements CommandLineRunner {

    private final UserMgmtService userMgmtService;
    private final RoleService roleService;

    @Override
    public void run(String... args) throws Exception {
        // Create or fetch roles

        roleService.seedRoleData(RoleRequestDTO.builder()
                .name(RoleType.PASSENGER) // Ensure this exists in your RoleType Enum
                .description("Cruise Passenger")
                .build());

        roleService.seedRoleData(RoleRequestDTO.builder()
                .name(RoleType.ADMIN) // Ensure this exists in your RoleType Enum
                .description("Cruise Admin")
                .build());

        roleService.seedRoleData(RoleRequestDTO.builder()
                .name(RoleType.CREW) // Ensure this exists in your RoleType Enum
                .description("Cruise Crew")
                .build());


        if (userMgmtService.isNewUser("admin")) {
            UserRegistrationRequestDTO adminReq = new UserRegistrationRequestDTO(
                    "admin",
                    "admin123",
                    "admin@cruisemate.com",
                    "Cruise Administrator",
                    RoleType.ADMIN
            );
            userMgmtService.registerNewUser(adminReq);
            log.info("Default CruiseMate Admin seeded successfully.");
        }

    }
}

