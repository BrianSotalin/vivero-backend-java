package com.vivero.vivero_backend.api.service;

import java.util.List;
import java.util.Optional;

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
 //Listar todos los usuarios
 public List<Usuario> getAllUsers() {	
	 return usuarioRepository.findAll();
 }
 //Crear un nuevo usuario		
 public Usuario create(Usuario usuario) {
	 return usuarioRepository.save(usuario);
 }
 //Buscar usuario por ID	
 public Optional<Usuario> getUserById(Long id) {
	 return usuarioRepository.findById(id);
 }
 //Eliminar usuario por ID
 public void delete(Long id) {	
	 if (!usuarioRepository.existsById(id)) {
		 throw new RuntimeException("Usuario no encontrado con ID: " + id);
	 }
	 usuarioRepository.deleteById(id);
 }
 @Transactional
 //Actualizar rol y reseteo de contraseña (Lógica actualizada)
 public Usuario updateUserRoleAndResetPassword(Long id, String nuevoRol, String nuevaPassword) {	
	 Usuario usuarioExistente = usuarioRepository.findById(id)
			 .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
	 
	// Si viene un rol válido y no vacío, lo cambia
	    if (nuevoRol != null && !nuevoRol.isBlank()) {
	        usuarioExistente.setRol(nuevoRol);
	    }
	    
	    // SOLO si viene una contraseña real, la encripta y la cambia. Si va vacía (""), la ignora.
	    if (nuevaPassword != null && !nuevaPassword.isBlank()) {
	        String passwordEncriptada = passwordEncoder.encode(nuevaPassword);
	        usuarioExistente.setPassword(passwordEncriptada);
	    }
	 
	 return usuarioRepository.save(usuarioExistente);
 }
}
