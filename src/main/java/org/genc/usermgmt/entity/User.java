package org.genc.usermgmt.entity;


import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    @Column(name = "full_name")
    private String fullName;
    private String phone;
    @Column(name = "passport_no")
    private String passportNo;
    @Column(name = "password_hash")
    private String password;
    @Column(name = "loyalty_points")
    private Integer loyaltyPoints;
    @Column(name = "is_active")
    private Integer isActive;
    @Column(name = "version_id")
    private Integer versionId;
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "role_id") // This links users.role_id to roles.id
private Role roles;

}