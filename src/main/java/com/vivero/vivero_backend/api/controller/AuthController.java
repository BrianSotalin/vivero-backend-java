package com.vivero.vivero_backend.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vivero.vivero_backend.api.dto.AuthResponses; // Verifica si es AuthResponse o AuthResponses
import com.vivero.vivero_backend.api.dto.LoginRequest;

import com.vivero.vivero_backend.api.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Autenticación", description = "Endpoints para login y gestión de sesión")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica al usuario con username y password. Devuelve un token JWT válido por 24 horas."
        )
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login exitoso, retorna JWT",
                content = @Content(schema = @Schema(implementation = AuthResponses.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas",
                content = @Content)
        })
    
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