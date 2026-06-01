package com.vivero.vivero_backend.api.service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.vivero.vivero_backend.api.model.Usuario;
import com.vivero.vivero_backend.api.repository.UsuarioRepository;
import jakarta.transaction.Transactional;

@Service
public class UserService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Reglas de contraseña
    private static final Pattern MAYUSCULA = Pattern.compile(".*[A-Z].*");
    private static final Pattern MINUSCULA = Pattern.compile(".*[a-z].*");
    private static final Pattern ESPECIAL  = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
    private static final int MIN_LENGTH = 8;

    private void validarPassword(String password) {
        if (password == null || password.length() < MIN_LENGTH)
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.");
        if (!MAYUSCULA.matcher(password).matches())
            throw new IllegalArgumentException("La contraseña debe tener al menos una letra mayúscula.");
        if (!MINUSCULA.matcher(password).matches())
            throw new IllegalArgumentException("La contraseña debe tener al menos una letra minúscula.");
        if (!ESPECIAL.matcher(password).matches())
            throw new IllegalArgumentException("La contraseña debe tener al menos un carácter especial.");
    }

    public List<Usuario> getAllUsers() {
        return usuarioRepository.findAll();
    }

    public Usuario create(Usuario usuario) {
        validarPassword(usuario.getPassword());
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> getUserById(Long id) {
        return usuarioRepository.findById(id);
    }

    public void delete(Long id) {
        if (!usuarioRepository.existsById(id))
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        usuarioRepository.deleteById(id);
    }

    @Transactional
    public Usuario updateUserRoleAndResetPassword(Long id, String nuevoRol, String nuevaPassword) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        if (nuevoRol != null && !nuevoRol.isBlank()) {
            usuarioExistente.setRol(nuevoRol);
        }

        if (nuevaPassword != null && !nuevaPassword.isBlank()) {
            validarPassword(nuevaPassword);
            usuarioExistente.setPassword(passwordEncoder.encode(nuevaPassword));
        }

        return usuarioRepository.save(usuarioExistente);
    }
}