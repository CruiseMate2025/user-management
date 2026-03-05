package org.genc.usermgmt.resource;

import org.genc.usermgmt.dto.loyaltyDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileManagementController {

    @GetMapping("/loyalty")
    public ResponseEntity<loyaltyDto> getLoyaltyProfile() {
        // Placeholder for actual loyalty profile retrieval logic
        loyaltyDto response = new loyaltyDto("Gold", 1500);
        return ResponseEntity.ok(response);
    }

}
