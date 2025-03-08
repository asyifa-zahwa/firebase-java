package com.example.cobafirebase.controllers;

import com.example.cobafirebase.services.OtpService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendOtp(@RequestParam String email) {
        return ResponseEntity.ok(otpService.sendOtp(email));
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp(@RequestParam String email, @RequestParam String otp) throws ExecutionException, InterruptedException {
        boolean isValid = otpService.verifyOtp(email, otp);
        return isValid ? ResponseEntity.ok("OTP valid!") : ResponseEntity.badRequest().body("OTP tidak valid atau expired");
    }
}
