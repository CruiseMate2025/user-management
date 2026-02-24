package org.genc.usermgmt.dto;

import lombok.Data;

@Data
public class AdminUpdateRequestDTO {
    private String fullName; // Matches entity @Column(name = "full_name")
    private String email;
    private String phone;
    private String username;
    private String password;
}