package com.vivero.vivero_backend.api.controller;


import com.vivero.vivero_backend.api.model.Usuario;
import com.vivero.vivero_backend.api.repository.UsuarioRepository;
import com.vivero.vivero_backend.api.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

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
@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema — solo ADMIN")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private UserService userService;
    
    @Operation(summary = "Listar todos los usuarios")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida")

	@GetMapping
		public List<Usuario> listar() {
		return usuarioRepository.findAll();
	}
    
    @Operation(summary = "Obtener usuario por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
	
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
	
    @Operation(
            summary = "Crear un nuevo usuario",
            description = "La contraseña debe tener mínimo 8 caracteres, una mayúscula, una minúscula y un carácter especial."
        )
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario creado con éxito"),
            @ApiResponse(responseCode = "400", description = "Contraseña no cumple las reglas de seguridad"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
    
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
    
    @Operation(
            summary = "Actualizar rol y/o contraseña",
            description = "Actualiza el rol del usuario y opcionalmente resetea la contraseña. Si la contraseña se envía vacía, no se modifica."
        )
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado con éxito"),
            @ApiResponse(responseCode = "400", description = "Contraseña no cumple las reglas de seguridad"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
        })

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
    
    @Operation(summary = "Eliminar un usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario eliminado con éxito"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    
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


