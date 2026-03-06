package org.genc.usermgmt.service.api;

import org.genc.usermgmt.dto.AdminUpdateRequestDTO;
import org.genc.usermgmt.dto.UserRegistrationRequestDTO;
import org.genc.usermgmt.dto.UserRegistrationResponseDTO;

public interface UserMgmtService {

    public UserRegistrationResponseDTO registerNewUser(UserRegistrationRequestDTO userReqDTO);

    public  boolean isNewUser(String userName);
    UserRegistrationResponseDTO updateUser(Long id, AdminUpdateRequestDTO request);

    /**
     * Get the loyalty points for a user by their ID.
     */
    int getLoyaltyPoints(Long userId);

    /**
     * Add loyalty points to a user's account (e.g., after successful payment).
     */
    int addLoyaltyPoints(Long userId, int points);

    /**
     * Redeem (deduct) loyalty points from a user's account.
     * Throws if user doesn't have enough points.
     */
    int redeemLoyaltyPoints(Long userId, int points);
}
