package org.genc.usermgmt.resource;

import lombok.RequiredArgsConstructor;
import org.genc.usermgmt.dto.AdminUpdateRequestDTO;
import org.genc.usermgmt.dto.UserProfileDTO;
import org.genc.usermgmt.dto.UserRegistrationResponseDTO;
import org.genc.usermgmt.entity.User;
import org.genc.usermgmt.repo.UserRepository;
import org.genc.usermgmt.service.api.UserMgmtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/userservice/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserMgmtService userMgmtService;
    private final UserRepository userRepository;

    @GetMapping("/settings/{id}")
    public ResponseEntity<UserProfileDTO> getAdminSettings(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        UserProfileDTO dto = UserProfileDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/settings/{id}")
    public ResponseEntity<UserRegistrationResponseDTO> updateAdminSettings(
            @PathVariable Long id,
            @RequestBody AdminUpdateRequestDTO request) {

        return ResponseEntity.ok(userMgmtService.updateUser(id, request));
    }
}