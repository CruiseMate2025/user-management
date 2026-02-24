package org.genc.usermgmt.resource;

import lombok.RequiredArgsConstructor;
import org.genc.usermgmt.dto.AdminUpdateRequestDTO;
import org.genc.usermgmt.dto.UserRegistrationResponseDTO;
import org.genc.usermgmt.service.api.UserMgmtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/userservice/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserMgmtService userMgmtService;

    @PatchMapping("/settings/{id}")
    public ResponseEntity<UserRegistrationResponseDTO> updateAdminSettings(
            @PathVariable Long id,
            @RequestBody AdminUpdateRequestDTO request) {

        return ResponseEntity.ok(userMgmtService.updateUser(id, request));
    }
}