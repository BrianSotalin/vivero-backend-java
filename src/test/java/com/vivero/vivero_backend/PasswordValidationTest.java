package com.vivero.vivero_backend;



import com.vivero.vivero_backend.api.model.Usuario;
import com.vivero.vivero_backend.api.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.vivero.vivero_backend.api.repository.UsuarioRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordValidationTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    // ─── CREATE: contraseñas inválidas ────────────────────────────────

    @Test
    void create_debeRechazarPasswordCorta() {
        Usuario u = new Usuario();
        u.setUsername("test");
        u.setPassword("Ab1!"); // menos de 8 caracteres

        assertThrows(IllegalArgumentException.class, () -> userService.create(u),
            "Debe lanzar excepción por contraseña corta");
    }

    @Test
    void create_debeRechazarPasswordSinMayuscula() {
        Usuario u = new Usuario();
        u.setUsername("test");
        u.setPassword("abcd1234!"); // sin mayúscula

        assertThrows(IllegalArgumentException.class, () -> userService.create(u),
            "Debe lanzar excepción por falta de mayúscula");
    }

    @Test
    void create_debeRechazarPasswordSinMinuscula() {
        Usuario u = new Usuario();
        u.setUsername("test");
        u.setPassword("ABCD1234!"); // sin minúscula

        assertThrows(IllegalArgumentException.class, () -> userService.create(u),
            "Debe lanzar excepción por falta de minúscula");
    }

    @Test
    void create_debeRechazarPasswordSinCaracterEspecial() {
        Usuario u = new Usuario();
        u.setUsername("test");
        u.setPassword("Abcde123"); // sin carácter especial

        assertThrows(IllegalArgumentException.class, () -> userService.create(u),
            "Debe lanzar excepción por falta de carácter especial");
    }

    @Test
    void create_debeAceptarPasswordValida() {
        Usuario u = new Usuario();
        u.setUsername("test");
        u.setPassword("Vivero@2024");

        when(passwordEncoder.encode(any())).thenReturn("hashed");
        when(usuarioRepository.save(any())).thenReturn(u);

        assertDoesNotThrow(() -> userService.create(u),
            "No debe lanzar excepción con contraseña válida");

        verify(passwordEncoder, times(1)).encode("Vivero@2024");
        verify(usuarioRepository, times(1)).save(u);
    }

    // ─── UPDATE: cambio de contraseña ────────────────────────────────

    @Test
    void update_debeRechazarNuevaPasswordInvalida() {
        Usuario existente = new Usuario();
        existente.setId(1L);
        existente.setUsername("admin");
        existente.setRol("ADMIN");
        existente.setPassword("hashedOld");

        when(usuarioRepository.findById(1L)).thenReturn(java.util.Optional.of(existente));

        assertThrows(IllegalArgumentException.class,
            () -> userService.updateUserRoleAndResetPassword(1L, "ADMIN", "sinreglas"),
            "Debe rechazar contraseña que no cumple las reglas");
    }

    @Test
    void update_debeAceptarNuevaPasswordValida() {
        Usuario existente = new Usuario();
        existente.setId(1L);
        existente.setUsername("admin");
        existente.setRol("ADMIN");
        existente.setPassword("hashedOld");

        when(usuarioRepository.findById(1L)).thenReturn(java.util.Optional.of(existente));
        when(passwordEncoder.encode(any())).thenReturn("hashedNew");
        when(usuarioRepository.save(any())).thenReturn(existente);

        assertDoesNotThrow(
            () -> userService.updateUserRoleAndResetPassword(1L, "ADMIN", "Nuevo@Pass1"),
            "No debe lanzar excepción con contraseña válida");

        verify(passwordEncoder, times(1)).encode("Nuevo@Pass1");
    }

    @Test
    void update_debeIgnorarPasswordVacia() {
        Usuario existente = new Usuario();
        existente.setId(1L);
        existente.setUsername("admin");
        existente.setPassword("hashedOld");

        when(usuarioRepository.findById(1L)).thenReturn(java.util.Optional.of(existente));
        when(usuarioRepository.save(any())).thenReturn(existente);

        assertDoesNotThrow(
            () -> userService.updateUserRoleAndResetPassword(1L, "USER", ""),
            "No debe validar si la contraseña viene vacía");

        verify(passwordEncoder, never()).encode(any());
    }
}