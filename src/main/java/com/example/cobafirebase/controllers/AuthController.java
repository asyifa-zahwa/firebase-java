package com.example.cobafirebase.controllers;

import com.example.cobafirebase.security.JwtUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public String login(@RequestParam String email) {
        // Anda bisa cek email di database (optional)
        return jwtUtil.generateToken(email);
    }
}
