package com.auth.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "admin123"; // your plain password
        String hashedPassword = encoder.encode(rawPassword);
        System.out.println(hashedPassword);
    }
}