package org.genc.usermgmt.dto;

import lombok.Builder;
import lombok.Data;
import org.genc.usermgmt.enums.RoleType;

@Data
@Builder
public class UserRegistrationResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String userMessage;
    private RoleType roles;
}
