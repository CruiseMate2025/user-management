package org.genc.usermgmt.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.genc.usermgmt.dto.AdminUpdateRequestDTO;
import org.genc.usermgmt.dto.UserRegistrationRequestDTO;
import org.genc.usermgmt.dto.UserRegistrationResponseDTO;
import org.genc.usermgmt.entity.User;
import org.genc.usermgmt.exception.UserAlreadyExistsException;
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
        } else {
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
                .roles(persUser.getRoles().getName()) // Wrap in a Set for DTO
                .userMessage(welcomeMessage)
                .build();
    }

    @Override
    public boolean isNewUser(String userName) {
        return userRepository.findByUsername(userName).isEmpty();
    }

    @Override
    public UserRegistrationResponseDTO updateUser(Long id, AdminUpdateRequestDTO request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully: {}", updatedUser.getUsername());

        return UserRegistrationResponseDTO.builder()
                .id(updatedUser.getId())
                .username(updatedUser.getUsername())
                .fullName(updatedUser.getFullName())
                .email(updatedUser.getEmail())
                .roles(updatedUser.getRoles().getName())
                .userMessage("User updated successfully")
                .build();
    }

    @Override
    public int getLoyaltyPoints(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return user.getLoyaltyPoints() != null ? user.getLoyaltyPoints() : 0;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public int addLoyaltyPoints(Long userId, int points) {
        if (points < 0)
            throw new IllegalArgumentException("Points to add must be non-negative");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        int current = user.getLoyaltyPoints() != null ? user.getLoyaltyPoints() : 0;
        user.setLoyaltyPoints(current + points);
        userRepository.save(user);
        log.info("Added {} loyalty points to user {}. New balance: {}", points, userId, user.getLoyaltyPoints());
        return user.getLoyaltyPoints();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public int redeemLoyaltyPoints(Long userId, int points) {
        if (points < 0)
            throw new IllegalArgumentException("Points to redeem must be non-negative");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        int current = user.getLoyaltyPoints() != null ? user.getLoyaltyPoints() : 0;
        if (points > current) {
            throw new RuntimeException("Insufficient loyalty points. Available: " + current + ", Requested: " + points);
        }
        user.setLoyaltyPoints(current - points);
        userRepository.save(user);
        log.info("Redeemed {} loyalty points from user {}. New balance: {}", points, userId, user.getLoyaltyPoints());
        return user.getLoyaltyPoints();
    }
}
