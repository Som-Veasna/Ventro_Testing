package com.ventro.ventro_testing.service.impl;

import com.ventro.ventro_testing.model.entity.User;
import com.ventro.ventro_testing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        System.out.println("=== DEBUG ===");
        System.out.println("EMAIL: " + email);
        System.out.println("USER FOUND: " + user);
        if (user != null) {
            System.out.println("PASSWORD: " + user.getPassword());
            System.out.println("IS ACTIVE: " + user.getIsActive());
            System.out.println("ROLE: " + user.getRole());
        }
        System.out.println("=============");
        if (user == null) throw new UsernameNotFoundException("User not found: " + email);
        return user;
    }

}