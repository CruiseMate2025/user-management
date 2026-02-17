//package org.genc.usermgmt.service.impl;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//import org.genc.usermgmt.dto.CustomUserDetails;
//import org.genc.usermgmt.entity.User;
//import org.genc.usermgmt.repo.UserRepository;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.stream.Collectors;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class CustomUserDetailsService implements UserDetailsService {
//    private final UserRepository userRepository;
//
//    @Override
//    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
//
//        log.info("User   found username {} , role s ize {}" , user.getUsername(),user.getRoles().size());
//      //  user.getRoles().forEach(r-> log.info("Role is {} ", r.getName()));
//       /* AbstractUserDetailsAuthenticationProvider.authenticate -->
//       DaoAuthenticationProvider.additionalAuthenticationChecks  requires password*/
//         new org.springframework.security.core.userdetails.User(
//                user.getUsername(),
//                user.getPassword(),
//                user.getRoles().stream()
//                        .map(role -> new SimpleGrantedAuthority(role.getName().toString()))
//                        .collect(Collectors.toList())
//        );
//
//         return new CustomUserDetails(user);
//    }
//}

package org.genc.usermgmt.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.genc.usermgmt.dto.CustomUserDetails;
import org.genc.usermgmt.entity.User;
import org.genc.usermgmt.repo.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Logic Change: Access the single 'role' instead of 'roles' collection
        if (user.getRoles() != null) {
            log.info("User found: {}, Assigned Role: {}", user.getUsername(), user.getRoles().getName());
        }

        /* Spring Security still expects a Collection of authorities.
           We wrap your single role into a list using Collections.singletonList.
        */
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(user.getRoles().getName().toString())
        );

        // Returning your custom wrapper
        return new CustomUserDetails(user);
    }
}