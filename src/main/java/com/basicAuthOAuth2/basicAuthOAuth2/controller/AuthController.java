package com.basicAuthOAuth2.basicAuthOAuth2.controller;


import com.basicAuthOAuth2.basicAuthOAuth2.dto.UserDto;
import com.basicAuthOAuth2.basicAuthOAuth2.model.User;
import com.basicAuthOAuth2.basicAuthOAuth2.service.UserService;
import io.swagger.v3.oas.annotations.OpenAPI31;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Registration", description = "Endpoints for user registration")
public class AuthController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDto userDto, BindingResult result) {
        // Check for validation errors
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));

            // If just one error, you can make it more specific for the frontend
            if (errors.size() == 1) {
                Map.Entry<String, String> entry = errors.entrySet().iterator().next();
                Map<String, String> formattedError = new HashMap<>();
                formattedError.put("message", entry.getValue());
                return ResponseEntity.badRequest().body(formattedError);
            }

            return ResponseEntity.badRequest().body(errors);
        }

        // Check if email already exists
        if (userService.existsByEmail(userDto.getEmail())) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Email is already registered");
            return ResponseEntity.badRequest().body(error);
        }

        // Register user
        User registeredUser = userService.register(userDto);
        return ResponseEntity.ok(registeredUser);
    }

}
