package org.genc.usermgmt.resource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.genc.usermgmt.service.api.UserMgmtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.base-path}/profile")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final UserMgmtService userMgmtService;
    String userid = "userId";
    String loyaltyPoints = "loyaltyPoints";
    /**
     * GET /api/v1/userservice/profile/loyalty/{userId}
     * Returns current loyalty points for the user.
     */
    @GetMapping("/loyalty/{userId}")
    public ResponseEntity<Map<String, Object>> getLoyaltyPoints(@PathVariable Long userId) {
        int points = userMgmtService.getLoyaltyPoints(userId);
        return ResponseEntity.ok(Map.of(userid, userId, loyaltyPoints, points));
    }

    /**
     * POST /api/v1/userservice/profile/loyalty/{userId}/add?points=100
     * Adds loyalty points to the user's balance.
     */
    @PostMapping("/loyalty/{userId}/add")
    public ResponseEntity<Map<String, Object>> addLoyaltyPoints(
            @PathVariable Long userId,
            @RequestParam int points) {
        int newBalance = userMgmtService.addLoyaltyPoints(userId, points);
        log.info("Added {} loyalty points to user {}. New balance: {}", points, userId, newBalance);
        return ResponseEntity.ok(Map.of(userid, userId, loyaltyPoints, newBalance));
    }

    /**
     * POST /api/v1/userservice/profile/loyalty/{userId}/redeem?points=500
     * Redeems loyalty points from the user's balance.
     */
    @PostMapping("/loyalty/{userId}/redeem")
    public ResponseEntity<Map<String, Object>> redeemLoyaltyPoints(
            @PathVariable Long userId,
            @RequestParam int points) {
        int newBalance = userMgmtService.redeemLoyaltyPoints(userId, points);
        log.info("Redeemed {} loyalty points from user {}. New balance: {}", points, userId, newBalance);
        return ResponseEntity.ok(Map.of(userid, userId, loyaltyPoints, newBalance));
    }
}
