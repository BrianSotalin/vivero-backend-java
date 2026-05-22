package com.vivero.vivero_backend.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vivero.vivero_backend.api.dto.AuthResponses; // Verifica si es AuthResponse o AuthResponses
import com.vivero.vivero_backend.api.dto.LoginRequest;

import com.vivero.vivero_backend.api.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            System.out.println("Intentando login para: " + request.getUsername());
            String token = authService.login(request.getUsername(), request.getPassword());
            
            return ResponseEntity.ok(new AuthResponses(token));
        } catch (Exception e) {
            System.err.println("Error en login: " + e.getMessage());
            e.printStackTrace(); 
            return ResponseEntity.status(401).body("Error: " + e.getMessage());
        }
    }


}