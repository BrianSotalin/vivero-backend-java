package com.vivero.vivero_backend.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vivero.vivero_backend.api.model.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
}
