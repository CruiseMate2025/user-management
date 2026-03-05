package org.genc.usermgmt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.genc.usermgmt.enums.RoleType;

@Data
@AllArgsConstructor
public class UserRegistrationRequestDTO {

    private String username;
    private String password;
    private String email;
    private String fullName;
    private RoleType roleType;
}
