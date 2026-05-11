package com.vivero.vivero_backend.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.vivero.vivero_backend.api.model.Usuario;
import com.vivero.vivero_backend.api.repository.UsuarioRepository;
import com.vivero.vivero_backend.api.config.JwtUtil;


@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil; 

    public String login(String username, String password) {
        // 1. Buscar usuario
        Usuario user = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Comparar contraseñas (Texto plano vs Hash)
        if (passwordEncoder.matches(password, user.getPassword())) {
            // 3. Generar Token
            return jwtUtil.generateTokenJWT(user); 
        } else {
            throw new RuntimeException("Credenciales incorrectas");
        }
    }
 // Añade este método a tu AuthService.java
    public Usuario registrar(Usuario usuario) {
        // 1. Encriptar la contraseña antes de guardar
        String passwordEncriptada = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(passwordEncriptada);
        
        // 2. Guardar en la base de datos
        return usuarioRepository.save(usuario);
    }
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("El usuario con ID " + id + " no existe.");
        }
        usuarioRepository.deleteById(id);
    }
}