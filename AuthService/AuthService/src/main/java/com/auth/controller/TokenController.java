package com.auth.controller;

import com.auth.service.JwtTokenService;
import com.auth.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import java.util.List;

@RestController
@RequestMapping("/api/token")
public class TokenController {

    private final JwtTokenService tokenService;
    private final UserService userService;

    public TokenController(JwtTokenService tokenService, UserService userService) {
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateToken(@RequestParam String username,
                                           @RequestParam String password) {

        if (!userService.validateUser(username, password)) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        List<String> roles = userService.getRoles(username);

        return ResponseEntity.ok(
                Map.of(
                        "accessToken", tokenService.generateAccessToken(username, roles),
                        "refreshToken", tokenService.generateRefreshToken(username)
                )
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestParam String refreshToken) {
        try {
            String username = tokenService.extractUsername(refreshToken);
            List<String> roles = userService.getRoles(username);

            String newAccessToken = tokenService.generateAccessToken(username, roles);

            return ResponseEntity.ok(Map.of(
                    "accessToken", newAccessToken
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid or expired refresh token");
        }
    }

    @GetMapping("/admin/secure")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminOnly() {
        return "Admin Access Granted ✅";
    }

    @PostMapping("/remaining-time")
    public ResponseEntity<?> tokenTime(@RequestParam String token) {

        long secondsLeft = tokenService.getRemainingTimeInSeconds(token);

        return ResponseEntity.ok(Map.of(
                "secondsLeft", secondsLeft,
                "minutesLeft", secondsLeft / 60
        ));
    }

    @GetMapping("/info")
    public Map<String, Object> tokenInfo(@AuthenticationPrincipal Jwt jwt) {

        if (jwt == null) {
            return Map.of("error", "JWT is null - token missing or invalid");
        }

        assert jwt.getExpiresAt() != null;
        assert jwt.getIssuedAt() != null;
        return Map.of(
                "expiresAt", jwt.getExpiresAt(),
                "issuedAt", jwt.getIssuedAt(),
                "subject", jwt.getSubject()
        );
    }
}
