package com.vivero.vivero_backend.api.controller;


import com.vivero.vivero_backend.api.model.Usuario;
import com.vivero.vivero_backend.api.repository.UsuarioRepository;
import com.vivero.vivero_backend.api.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios") // Esta ruta requiere TOKEN
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private UserService userService;

	@GetMapping
		public List<Usuario> listar() {
		return usuarioRepository.findAll();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
		try {
			Usuario usuario = userService.getUserById(id)
					.orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
			return ResponseEntity.ok(usuario);
		} catch (Exception e) {
			return ResponseEntity.status(404).body(e.getMessage());
		}
	}
	
	@PostMapping
	public ResponseEntity<?> crear(@RequestBody Usuario usuario) {
	    try {
	        Usuario nuevoUsuario = userService.create(usuario);
	        return ResponseEntity.ok(nuevoUsuario);
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
	    } catch (Exception e) {
	        return ResponseEntity.status(500).body(Map.of("message", "Error interno del servidor."));
	    }
	}

	@PatchMapping("/{id}")
	public ResponseEntity<?> editarParcial(@PathVariable Long id, @RequestBody Usuario usuario) {
	    try {
	        Usuario actualizado = userService.updateUserRoleAndResetPassword(id, usuario.getRol(), usuario.getPassword());
	        return ResponseEntity.ok(actualizado);
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
	    } catch (Exception e) {
	        return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
	    }
	}
	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(@PathVariable Long id) {
				try {
					userService.delete(id);
			 return ResponseEntity.ok(Map.of("message", "Cliente eliminado con éxito"));
		} catch (Exception e) {
			return ResponseEntity.status(404).body(e.getMessage());
		}
	}
}


