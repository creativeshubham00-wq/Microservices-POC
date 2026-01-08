package com.auth.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
public class JwtTokenService {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public JwtTokenService(KeyPair keyPair) {
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
    }

    // ================= ACCESS TOKEN =================
    public String generateAccessToken(String username, List<String> roles) {

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles) // used by Spring Security
                .setIssuedAt(new Date())
                .setExpiration(
                        Date.from(Instant.now().plus(15, ChronoUnit.MINUTES))
                )
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    // ================= REFRESH TOKEN =================
    public String generateRefreshToken(String username) {

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(
                        Date.from(Instant.now().plus(7, ChronoUnit.DAYS))
                )
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    // ================= TOKEN VALIDATION =================
    public String extractUsername(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public long getRemainingTimeInSeconds(String token) {

        var claims = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        long exp = claims.getExpiration().getTime();
        long now = System.currentTimeMillis();

        return (exp - now) / 1000;
    }

}
