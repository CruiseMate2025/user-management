package org.genc.usermgmt.entity;

import jakarta.persistence.*;
import lombok.*;
import org.genc.usermgmt.enums.RoleType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING) // If using Enum
    @Column(name = "name", length = 50, nullable = false, unique = true)
    private RoleType name;

    private String description;
}
