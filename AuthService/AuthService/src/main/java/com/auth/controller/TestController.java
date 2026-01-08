package com.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/public")
    public String publicEndpoint() {
        return "This is public!";
    }

    @GetMapping("/protected")
    public String protectedEndpoint() {
        return "This is protected!";
    }
}
