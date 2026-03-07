package org.genc.usermgmt.dto;

import lombok.Builder;
import lombok.Data;
import org.genc.usermgmt.enums.RoleType;

@Data
@Builder
public class AdminUpdateResponseDTO {
    private Long id;
    private String fullName; // Matches entity @Column(name = "full_name")
    private String email;
    private String phone;
    private String username;
    private String password;
    private String description;
    private RoleType roles;
    private String userMessage;
}
