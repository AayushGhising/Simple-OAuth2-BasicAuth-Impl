package com.basicAuthOAuth2.basicAuthOAuth2.controller;

import com.basicAuthOAuth2.basicAuthOAuth2.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class OtpController {

    @Autowired
    private OtpService otpService;

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(HttpSession session) {
        String email = (String) session.getAttribute("PENDING_AUTH_EMAIL");
        if (email == null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "No pending authentication found");
            return ResponseEntity.badRequest().body(response);
        }

        otpService.generateAndSendOtp(email);

        Map<String, String> response = new HashMap<>();
        response.put("message", "OTP sent to your email");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String otp, HttpSession session) {
        String email = (String) session.getAttribute("PENDING_AUTH_EMAIL");

        if (email == null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "No pending authentication found");
            return ResponseEntity.badRequest().body(response);
        }

        if (otpService.validateOtp(email, otp)) {
            // Set the OTP_VERIFIED flag that your filter is looking for
            session.setAttribute("OTP_VERIFIED", true);

            Map<String, String> response = new HashMap<>();
            response.put("message", "OTP verified successfully");
            response.put("redirectUrl", "/welcome");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Invalid OTP");
            return ResponseEntity.badRequest().body(response);
        }
    }
}