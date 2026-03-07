package org.genc.usermgmt.resource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.genc.usermgmt.dto.*;
import org.genc.usermgmt.entity.User;
import org.genc.usermgmt.repo.UserRepository;
import org.genc.usermgmt.service.api.UserMgmtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/userservice/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserRepository userRepository;
    private final UserMgmtService userMgmtService;

    /**
     * GET /api/v1/userservice/users — list all users (admin)
     */
    @GetMapping
    public ResponseEntity<List<UserProfileDTO>> getAllUsers() {
        List<UserProfileDTO> users = userRepository.findAll().stream()
                .map(this::toProfileDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/v1/userservice/users/{id} — get a single user by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDTO> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return ResponseEntity.ok(toProfileDTO(user));
    }

    /**
     * PUT /api/v1/userservice/users/{id} — update user fields (admin or self)
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserProfileDTO> updateUser(
            @PathVariable Long id,
            @RequestBody AdminUpdateRequestDTO request) {
        AdminUpdateResponseDTO res = userMgmtService.updateUser(id, request);
        User updated = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        log.info("User {} updated successfully", id);
        return ResponseEntity.ok(toProfileDTO(updated));
    }

    /**
     * DELETE /api/v1/userservice/users/{id} — delete a user (admin)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        log.info("User {} deleted", id);
        return ResponseEntity.noContent().build();
    }

    private UserProfileDTO toProfileDTO(User user) {
        return UserProfileDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .passportNo(user.getPassportNo())
                .loyaltyPoints(user.getLoyaltyPoints() != null ? user.getLoyaltyPoints() : 0)
                .role(user.getRoles() != null ? capitalize(user.getRoles().getName().name()) : "Passenger")
                .status(user.getIsActive() != null && user.getIsActive() == 1 ? "Active" : "Inactive")
                .build();
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
