package com.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyPair;
import java.util.Base64;

@RestController
@RequestMapping
public class JwksController {

    private final KeyPair keyPair;

    public JwksController(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    @GetMapping("/public-key")
    public String publicKey() {
        return Base64.getEncoder()
                     .encodeToString(keyPair.getPublic().getEncoded());
    }
}
