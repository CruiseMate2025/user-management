package org.genc.usermgmt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.genc.usermgmt.enums.RoleType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String userMessage;
    private RoleType roles;
}
