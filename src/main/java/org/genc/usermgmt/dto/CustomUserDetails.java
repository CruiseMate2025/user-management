//package org.genc.usermgmt.dto;
//
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.Setter;
//import org.genc.usermgmt.entity.User;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.util.Collection;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Getter
//@Setter
//public class CustomUserDetails  implements UserDetails {
//    private final String username;
//    private final String password;
//    private final Set<GrantedAuthority> authorities;
//    // Additional fields
//    private final String email;
//    private final String firstName;
//    private String lastName;
//    private String phone;
//
//    public CustomUserDetails(User user) {
//        this.authorities =  user.getRoles().stream()
//                .map(role -> new SimpleGrantedAuthority(role.getName().toString()))
//                .collect(Collectors.toSet());
//        this.username = user.getUsername();
//        this.password = user.getPassword();
//        this.email = user.getEmail();
//        this.firstName = user.getFullName();
//        this.phone = user.getPhone();
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return authorities;
//    }
//
//    @Override
//    public String getPassword() {
//        return password;
//    }
//
//    @Override
//    public String getUsername() {
//        return username;
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
//}

package org.genc.usermgmt.dto;

import lombok.Getter;
import lombok.Setter;
import org.genc.usermgmt.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Getter
@Setter
public class CustomUserDetails implements UserDetails {
    private final Long id;
    private final String username;
    private final String password;
    private final Set<GrantedAuthority> authorities;
    private final String email;
    private String fullName;
    private String phone;

    public CustomUserDetails(User user) {
        if (user.getRoles() != null) {
            this.authorities = Collections.singleton(
                    new SimpleGrantedAuthority("ROLE_" + user.getRoles().getName().toString())
            );
        } else {
            this.authorities = Collections.emptySet();
        }
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.fullName = user.getFullName();
        this.phone = user.getPhone();
        this.id = user.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    // Standard UserDetails overrides...
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}