package org.genc.usermgmt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String passportNo;
    private Integer loyaltyPoints;
    private String role;
    private String status; // "Active" or "Inactive"
    private String password;
}
