package org.genc.usermgmt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.genc.usermgmt.enums.RoleType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationRequestDTO {

    private String username;
    private String password;
    private String email;
    private String fullName;
    private RoleType roleType;
}
