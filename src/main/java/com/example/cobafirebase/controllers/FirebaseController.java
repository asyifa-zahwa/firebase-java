package com.example.cobafirebase.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.cobafirebase.dto.AuthResponse;
import com.example.cobafirebase.dto.LoginRequest;
import com.example.cobafirebase.dto.UserRequest;
import com.example.cobafirebase.security.JwtUtil;
import com.example.cobafirebase.services.FirebaseService;

import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


// @RestController
// @RequestMapping("/api")
// public class FirebaseController {

//     private final FirebaseService firebaseService;

//     public FirebaseController(FirebaseService firebaseService) {
//         this.firebaseService = firebaseService;
//     }

//     @PostMapping("/register")
//     public ResponseEntity<String> registerUser(@RequestBody UserRequest userRequest) {
//         String result = firebaseService.registerUser(userRequest);
//         return ResponseEntity.ok(result);
//     }

//     @PostMapping("/login")
//     public ResponseEntity<String> postMethodName(@RequestBody String usernameOrEmail, @RequestBody String password) {
//         //TODO: process POST request
        
//         return ResponseEntity.ok("oke");
//     }
   
//     @GetMapping("/{userId}")
//     public ResponseEntity<Map<String, Object>> getUser(@PathVariable String userId) {
//         Map<String, Object> userData = firebaseService.getUser(userId);
//         return userData != null ? ResponseEntity.ok(userData) : ResponseEntity.notFound().build();
//     }

//     @DeleteMapping("/{userId}")
//     public ResponseEntity<String> deleteUser(@PathVariable String userId) {
//         String result = firebaseService.deleteUser(userId);
//         return ResponseEntity.ok(result);
//     }
// }
@RestController
@RequestMapping("/api")
public class FirebaseController {

    private final FirebaseService firebaseService;
    private final JwtUtil jwtUtil;

    public FirebaseController(FirebaseService firebaseService, JwtUtil jwtUtil) {
        this.firebaseService = firebaseService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRequest userRequest) {
        String result = firebaseService.registerUser(userRequest);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String result = firebaseService.authenticateUser(loginRequest.getUsernameOrEmail(), loginRequest.getPassword());

        if (result.equals("Login berhasil")) {
            String token = jwtUtil.generateToken(loginRequest.getUsernameOrEmail());
            return ResponseEntity.ok(new AuthResponse(token));
        } else {
            return ResponseEntity.status(401).body("Login gagal: " + result);
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable String userId) {
        Map<String, Object> userData = firebaseService.getUser(userId);
        return userData != null ? ResponseEntity.ok(userData) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable String userId) {
        String result = firebaseService.deleteUser(userId);
        return ResponseEntity.ok(result);
    }
}
