package org.genc.usermgmt.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.genc.usermgmt.dto.UserRegistrationRequestDTO;
import org.genc.usermgmt.dto.UserRegistrationResponseDTO;
import org.genc.usermgmt.entity.Role;
import org.genc.usermgmt.entity.User;
import org.genc.usermgmt.enums.RoleType;
import org.genc.usermgmt.exception.UserAlreadyExistsException;
import org.genc.usermgmt.repo.RoleRepository;
import org.genc.usermgmt.repo.UserRepository;
import org.genc.usermgmt.service.api.RoleService;
import org.genc.usermgmt.service.api.UserMgmtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserMgmtServiceImpl implements UserMgmtService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserRegistrationResponseDTO registerNewUser(UserRegistrationRequestDTO userReqDTO) {
        Optional<User> existingUser = userRepository.findByUsername(userReqDTO.getUsername());
        User persUser;

        if (existingUser.isPresent()) {
            User userEntity = existingUser.get();

            // 1. Fix: Check single role instead of Set
            if (userEntity.getRoles() != null && userEntity.getRoles().getName().equals(userReqDTO.getRoleType())) {
                log.error("{} already exists with role {}", userReqDTO.getUsername(), userReqDTO.getRoleType());
                throw new UserAlreadyExistsException(userReqDTO.getUsername() + " already exists");
            }

            // 2. Fix: Overwrite the role (since ManyToOne allows only one role per user)
            userEntity.setRoles(roleService.getRoleByName(userReqDTO.getRoleType()));
            persUser = userRepository.save(userEntity);
            log.info("Role updated to {} for existing user {}", userReqDTO.getRoleType(), userEntity.getFullName());
        }
        else {
            // 3. Fix: Use .role() instead of .roles() in the builder
            User user = User.builder()
                    .username(userReqDTO.getUsername())
                    .password(passwordEncoder.encode(userReqDTO.getPassword()))
                    .fullName(userReqDTO.getFullName())
                    .email(userReqDTO.getEmail())
                    .roles(roleService.getRoleByName(userReqDTO.getRoleType())) // Changed to singular
                    .isActive(1)
                    .loyaltyPoints(0)
                    .versionId(1)
                    .build();

            persUser = userRepository.save(user);
            log.info("New CruiseMate account created: {}", persUser.getUsername());
        }

        String welcomeMessage = String.format("Welcome %s! Start your professional maritime journey with CruiseMate.",
                persUser.getFullName());

        // 4. Fix: Map single role to a Set for the Response DTO (to keep the DTO contract the same)
        return UserRegistrationResponseDTO.builder()
                .id(persUser.getId())
                .username(persUser.getUsername())
                .fullName(persUser.getFullName())
                .email(persUser.getEmail())
                .roles(java.util.Set.of(persUser.getRoles().getName())) // Wrap in a Set for DTO
                .userMessage(welcomeMessage)
                .build();
    }
    private boolean isUserRoleExists(User userObj, RoleType newRole) {
        return userObj.getRoles() != null && userObj.getRoles().getName().equals(newRole);
    }
    @Override
    public boolean isNewUser(String userName) {
        return userRepository.findByUsername(userName).isEmpty();
    }
}