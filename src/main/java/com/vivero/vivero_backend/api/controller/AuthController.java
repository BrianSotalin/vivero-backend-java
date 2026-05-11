package com.vivero.vivero_backend.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vivero.vivero_backend.api.dto.AuthResponses; // Verifica si es AuthResponse o AuthResponses
import com.vivero.vivero_backend.api.dto.LoginRequest;
import com.vivero.vivero_backend.api.model.Usuario;
import com.vivero.vivero_backend.api.service.AuthService;

@RestController
@RequestMapping("/api/auth")
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
 // Añade este método a tu AuthController.java
    @PostMapping("/register")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        try {
            Usuario nuevoUsuario = authService.registrar(usuario);
            return ResponseEntity.ok("Usuario registrado con éxito: " + nuevoUsuario.getUsername());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al registrar: " + e.getMessage());
        }
    }

}