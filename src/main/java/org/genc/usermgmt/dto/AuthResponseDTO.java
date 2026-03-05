package org.genc.usermgmt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDTO {
    private Long id;
    private String jwt;
    private String fullName;
    private String role;
    private String email;
    private String phone;
    private String appInstance;

}
