package com.auth.service;

import com.auth.entities.User;
import com.auth.entities.UserRole;
import com.auth.repository.UserRepository;
import com.auth.repository.UserRolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRolesRepository rolesRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean validateUser(String username, String rawPassword) {
        User user = userRepository.findByUsername(username).orElse(null);
        return user != null && passwordEncoder.matches(rawPassword, user.getPassword());
    }

    public List<String> getRoles(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        return rolesRepository.findByUserId(user.getId())
                .stream()
                .map(UserRole::getRoleName)
                .collect(Collectors.toList());
    }
}
